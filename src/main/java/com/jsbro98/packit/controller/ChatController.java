package com.jsbro98.packit.controller;

import com.jsbro98.packit.mock.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class ChatController {

  @MessageMapping("/chat-session")
  @SendTo("/topic/chat")
  public String chatSession(Message message) throws Exception {
    return HtmlUtils.htmlEscape(message.message());
  }
}
