package com.jsbro98.packit.engine.impl;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.errors.ListenerFailedException;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.ChatPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
// @Profile("dev") enable when implementation is made
public class SimpleChatEngine implements ChatEngine {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChatEngine.class);

  private final CopyOnWriteArrayList<MessageListener> listeners;

  public SimpleChatEngine() {
    listeners = new CopyOnWriteArrayList<>();
  }

  @Override
  public void sendMessage(ChatMessage message) {
    if (ChatPayload.isInvalid(message)) {
      LOGGER.error("Message {} is invalid", message);
      throw new IllegalArgumentException("Message is invalid");
    }

    LOGGER.debug("Sending message: {}", message);
    for (MessageListener listener : listeners) {
      try {
        listener.onMessage(message);
      } catch (Exception e) {
        LOGGER.error("Listener failed for message {}: {}", message.id(), e.getMessage(), e);
        throw new ListenerFailedException("Listener failed for message " + message.id(), e);
      }
    }
  }

  @Override
  public void registerListener(MessageListener listener) {
    if (listener == null) {
      LOGGER.error("Listener is null");
      throw new IllegalArgumentException("listener cannot be null");
    }

    if (!listeners.addIfAbsent(listener)) {
      LOGGER.warn("Listener is already registered: {}", listener);
    }
  }
}
