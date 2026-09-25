package com.jsbro98.packit.unit;

import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.engine.impl.SimpleChatEngine;
import com.jsbro98.packit.errors.ListenerFailedException;
import com.jsbro98.packit.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleChatEngineTest {
  private SimpleChatEngine chatEngine;
  private boolean listenerFiredFlag = false;

  private static Stream<ChatMessage> invalidMessages() {
    return Stream.of(
            new ChatMessage(UUID.randomUUID(), Instant.now(), null, "content"),
            new ChatMessage(UUID.randomUUID(), Instant.now(), "", "content"),
            new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", null),
            new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", "")
    );
  }

  @BeforeEach
  void setUp() {
    chatEngine = new SimpleChatEngine();
    chatEngine.registerListener(message -> listenerFiredFlag = true);
    listenerFiredFlag = false;
  }

  @Test
  void sendMessage_shouldReturnTrue_WhenGivenAValidMessage() {
    ChatMessage message = new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", "Testing...");

    chatEngine.sendMessage(message);

    assertTrue(listenerFiredFlag);
  }

  @ParameterizedTest
  @MethodSource("invalidMessages")
  void sendMessage_shouldThrow_WhenGivenInvalidMessage(ChatMessage message) {
    assertThrows(IllegalArgumentException.class, () -> chatEngine.sendMessage(message));
  }

  @Test
  void sendMessage_shouldThrow_WhenMessageIsNull() {
    assertThrows(IllegalArgumentException.class, () -> chatEngine.sendMessage(null));
  }

  @Test
  void registerListener_shouldThrow_WhenGivenANullListener() {
    MessageListener listener = null;

    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> chatEngine.registerListener(listener));
    assertEquals("listener cannot be null", ex.getMessage());
  }

  @Test
  void sendMessage_shouldThrow_whenABadListenerIsRegistered() {
    MessageListener listener = (message) -> {
      throw new RuntimeException("BOOM!");
    };
    ChatMessage message = new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", "Testing...");

    chatEngine.registerListener(listener);

    assertThrows(ListenerFailedException.class, () -> chatEngine.sendMessage(message));
  }
}