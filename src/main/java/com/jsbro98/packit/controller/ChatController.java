package com.jsbro98.packit.controller;

import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// TODO: create controller advice for this class
@Controller
public class ChatController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @MessageMapping("/send")
  public void handleMessage(SendMessageRequest sendMessageRequest) {
    LOGGER.debug("Received message: {}", sendMessageRequest);
    chatService.processMessage(sendMessageRequest);
  }
}
