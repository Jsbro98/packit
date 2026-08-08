package com.jsbro98.packit.service;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

  private final ChatEngine chatEngine;
  private final MessageStore messageStore;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatService(ChatEngine chatEngine,
                     MessageStore messageStore,
                     SimpMessagingTemplate messagingTemplate) {
    this.chatEngine = chatEngine;
    this.messageStore = messageStore;
    this.messagingTemplate = messagingTemplate;

    initializeListeners();
  }

  public void processMessage(SendMessageRequest request) {
    validate(request);
    LOGGER.debug("Processing a message: {}", request);
    var chatMessage = ChatMessage.create(request);
    attemptSendAndSave(chatMessage);
  }

  private void validate(SendMessageRequest request) {
    if (request.sender() == null || request.sender().isBlank()
            || request.content() == null || request.content().isBlank()) {
      throw new IllegalArgumentException("sender and content must not be blank");
    }
  }

  private void attemptSendAndSave(ChatMessage message) {
    if (chatEngine.sendMessage(message)) {
      LOGGER.debug("Message {} sent successfully", message.id());

      // intentionally unguarded, DB will throw
      messageStore.saveMessage(message);
    } else {
      LOGGER.error("Broadcast failed for message {}; skipping persistence", message.id());
    }
  }

  // TODO: possibly move this to ChatEngine
  private void initializeListeners() {
    // only listener for now is serializing and re-sending to frontend's "/topic"
    chatEngine.registerListener(msg ->
            messagingTemplate.convertAndSend("/topic/messages", msg));
  }
}
