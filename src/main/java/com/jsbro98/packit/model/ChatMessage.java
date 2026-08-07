package com.jsbro98.packit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

public record ChatMessage(UUID id, Instant timestamp, String sender, String content) {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChatMessage.class);

  public static ChatMessage create(String sender, String content) {
    LOGGER.debug("Creating ChatMessage with sender: {}, content: {}", sender, content);
    return new ChatMessage(UUID.randomUUID(), Instant.now(), sender, content);
  }

  public static ChatMessage create(SendMessageRequest sendMessageRequest) {
    LOGGER.debug("Creating ChatMessage with message: {}", sendMessageRequest);
    return new ChatMessage(UUID.randomUUID(), Instant.now(), sendMessageRequest.sender(), sendMessageRequest.content());
  }
}
