package com.jsbro98.packit.controller;

import com.jsbro98.packit.errors.InvalidMessageException;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @MessageMapping("/send")
  public void handleMessage(@Payload SendMessageRequest sendMessageRequest) {
    LOGGER.debug("Received message: {}", sendMessageRequest);
    chatService.processMessage(sendMessageRequest);
  }

  // invalid user requests are expected. swallow and log from controller
  // InvalidMessageException is thrown on construction in ChatMessage
  @MessageExceptionHandler(InvalidMessageException.class)
  public void onInvalid(InvalidMessageException e) {
    LOGGER.warn("Invalid message request: {}", e.getMessage());
  }
}
