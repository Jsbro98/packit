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

  public ChatService(ChatEngine chatEngine, MessageStore messageStore, SimpMessagingTemplate messagingTemplate) {
    this.chatEngine = chatEngine;
    this.messageStore = messageStore;

    registerBroadcastListener(chatEngine, messagingTemplate);
  }

  // TODO: fix registering listeners here. Originally this was needed because the engine
  //  needs to know the Messaging template, but different ChatEngines will have different MessageListeners
  private static void registerBroadcastListener(ChatEngine engine, SimpMessagingTemplate template) {
    engine.registerListener(msg -> template.convertAndSend("/topic/messages", msg));
  }

  public void processMessage(SendMessageRequest request) {
    var chatMessage = ChatMessage.create(request.sender(), request.content());
    LOGGER.debug("Created message {}", chatMessage.id());
    saveAndSend(chatMessage);
  }

  private void saveAndSend(ChatMessage message) {
    messageStore.saveMessage(message);
    chatEngine.sendMessage(message);
    LOGGER.debug("Message {} saved and sent", message.id());
  }
}
