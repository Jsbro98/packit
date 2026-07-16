package com.jsbro98.packit.service;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.model.Message;
import com.jsbro98.packit.store.MessageStore;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
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

  public void processMessage(Message message) {
    messageStore.saveMessage(message);
    chatEngine.sendMessage(message);
  }

  private void initializeListeners() {
    // only listener for now is serializing and re-sending to frontend's "/topic"
    chatEngine.registerListener(msg ->
            messagingTemplate.convertAndSend("/topic/messages", msg));
  }
}
