package com.jsbro98.packit.engine.impl;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.api.MessageListener;
import com.jsbro98.packit.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
// @Profile("dev") enable when implementation is made
public class SimpleChatEngine implements ChatEngine {
  private final List<MessageListener> listeners;

  SimpleChatEngine() {
    listeners = new ArrayList<>();
  }

  @Override
  public void sendMessage(Message message) {
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
