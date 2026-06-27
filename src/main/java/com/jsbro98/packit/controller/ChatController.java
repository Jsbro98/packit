package com.jsbro98.packit.controller;

import com.jsbro98.packit.mock.api.ChatEngine;
import com.jsbro98.packit.mock.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private final ChatEngine chatEngine;
  private final SimpMessagingTemplate messagingTemplate;

  public ChatController(ChatEngine chatEngine, SimpMessagingTemplate messagingTemplate) {
    this.chatEngine = chatEngine;
    this.messagingTemplate = messagingTemplate;
    // only listener for now is serializing and sending to the topic
    chatEngine.registerListener(msg ->
            messagingTemplate.convertAndSend("/topic/messages", msg));
  }

  @MessageMapping("/send")
  public void handleMessage(Message message) {
    chatEngine.sendMessage(message);
  }
}
