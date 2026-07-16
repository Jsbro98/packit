package com.jsbro98.packit.controller;

import com.jsbro98.packit.model.Message;
import com.jsbro98.packit.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @MessageMapping("/send")
  public void handleMessage(Message message) {
    chatService.processMessage(message);
  }
}
