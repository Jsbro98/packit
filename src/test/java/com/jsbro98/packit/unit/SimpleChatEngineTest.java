package com.jsbro98.packit.unit;

import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.engine.impl.SimpleChatEngine;
import com.jsbro98.packit.errors.ListenerFailedException;
import com.jsbro98.packit.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SimpleChatEngineTest {
  private SimpleChatEngine chatEngine;
  private boolean listenerFiredFlag = false;

  @BeforeEach
  void setUp() {
    chatEngine = new SimpleChatEngine();
    chatEngine.registerListener(_ -> listenerFiredFlag = true);
    listenerFiredFlag = false;
  }

  @Test
  void sendMessage_shouldReturnTrue_whenGivenAValidMessage() {
    ChatMessage message = new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", "Testing...");

    chatEngine.sendMessage(message);

    assertTrue(listenerFiredFlag);
  }

  @Test
  void registerListener_shouldThrow_whenGivenANullListener() {
    MessageListener listener = null;

    IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> chatEngine.registerListener(listener));
    assertEquals("listener cannot be null", ex.getMessage());
  }

  @Test
  void sendMessage_shouldThrow_whenABadListenerIsRegistered() {
    MessageListener listener = _ -> {
      throw new RuntimeException("BOOM!");
    };
    ChatMessage message = new ChatMessage(UUID.randomUUID(), Instant.now(), "Bob", "Testing...");

    chatEngine.registerListener(listener);

    assertThrows(ListenerFailedException.class, () -> chatEngine.sendMessage(message));
  }
}