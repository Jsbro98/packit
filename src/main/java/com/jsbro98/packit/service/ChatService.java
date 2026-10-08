package com.jsbro98.packit.service;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.store.MessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

  private final ChatEngine chatEngine;
  private final MessageStore messageStore;

  public ChatService(ChatEngine chatEngine, MessageStore messageStore) {
    this.chatEngine = chatEngine;
    this.messageStore = messageStore;
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
