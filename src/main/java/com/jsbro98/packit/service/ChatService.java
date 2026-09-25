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
    LOGGER.debug("Validating incoming request: {}", request);

    // this just returns instead of throwing because invalid requests from users are expected
    if (SendMessageRequest.isMessageRequestInvalid(request)) {
      LOGGER.warn("Invalid message request was received: {}", request);
      return;
    }

    LOGGER.debug("Processing a message: {}", request);
    var chatMessage = ChatMessage.create(request);
    LOGGER.debug("Transformed request into a message: {}", chatMessage);
    attemptSendAndSave(chatMessage);
  }

  private void attemptSendAndSave(ChatMessage message) {
    chatEngine.sendMessage(message);
    LOGGER.debug("Message {} sent successfully", message.id());

    // intentionally unguarded, DB will throw
    messageStore.saveMessage(message);
  }

  /*
       service registers chatEngine's listeners because the service
       owns the connection between the chatEngine and the
       messaging template
  */
  private void initializeListeners() {
    // only listener for now is serializing and re-sending to frontend's "/topic"
    chatEngine.registerListener(msg ->
            messagingTemplate.convertAndSend("/topic/messages", msg));
  }
}
