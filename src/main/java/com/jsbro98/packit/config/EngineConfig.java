package com.jsbro98.packit.config;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.impl.SimpleChatEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class EngineConfig {
  @Bean
  @Primary
  public ChatEngine simpleChatEngine(SimpMessagingTemplate template) {
    ChatEngine engine = new SimpleChatEngine();
    engine.registerListener(msg -> template.convertAndSend("/topic/messages", msg));
    return engine;
  }
}
