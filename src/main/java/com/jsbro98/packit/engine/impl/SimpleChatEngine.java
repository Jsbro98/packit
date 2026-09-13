package com.jsbro98.packit.engine.impl;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
// @Profile("dev") enable when implementation is made
public class SimpleChatEngine implements ChatEngine {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleChatEngine.class);

  private final List<MessageListener> listeners;

  public SimpleChatEngine() {
    listeners = new ArrayList<>();
  }

  @Override
  public boolean sendMessage(ChatMessage message) {
    if (ChatMessage.isMessageInvalid(message)) {
      LOGGER.warn("Invalid message received: {}", message);
      return false;
    }

    LOGGER.debug("Sending message: {}", message);
    for (MessageListener listener : listeners) {
      try {
        listener.onMessage(message);
      } catch (Exception e) {
        LOGGER.error("Listener failed for message {}: {}", message.id(), e.getMessage(), e);
        return false;
      }
    }
    return true;
  }

  @Override
  public void registerListener(MessageListener listener) {
    if (listener == null) {
      LOGGER.error("Listener is null");
      throw new IllegalArgumentException("listener cannot be null");
    }

    if (listeners.contains(listener)) {
      LOGGER.warn("Listener is already registered: {}", listener);
      return;
    }

    listeners.add(listener);
  }
}
