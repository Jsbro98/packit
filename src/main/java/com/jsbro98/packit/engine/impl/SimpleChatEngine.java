package com.jsbro98.packit.engine.impl;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.model.Message;
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

  SimpleChatEngine() {
    listeners = new ArrayList<>();
  }

  @Override
  public void sendMessage(Message message) {
    LOGGER.debug("Sending message: {}", message);
    for (MessageListener listener : listeners) {
      listener.onMessage(message);
    }
  }

  @Override
  public void registerListener(MessageListener listener) {
    if (listeners.contains(listener)) {
      return;
    }

    listeners.add(listener);
  }
}
