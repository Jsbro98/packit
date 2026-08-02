package com.jsbro98.packit.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketConfig.class);

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    LOGGER.debug("Registering WebSocket API endpoints");
    registry.addEndpoint("/ws");
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    LOGGER.debug("Configuring WebSocket API endpoints");
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }
}
