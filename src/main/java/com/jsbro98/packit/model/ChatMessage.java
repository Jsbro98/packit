package com.jsbro98.packit.model;

import com.jsbro98.packit.errors.InvalidMessageException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ChatMessage(UUID id, Instant timestamp, String sender, String content) {

  public ChatMessage {
    Objects.requireNonNull(id, "id was null");
    Objects.requireNonNull(timestamp, "timestamp  was null");
    if (isBlank(sender) || isBlank(content)) {
      throw new InvalidMessageException("sender and content must not be blank or null");
    }
  }

  public static ChatMessage create(String sender, String content) {
    return new ChatMessage(UUID.randomUUID(), Instant.now(), sender, content);
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }
}
