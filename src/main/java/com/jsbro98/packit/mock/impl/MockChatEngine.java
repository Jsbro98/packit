package com.jsbro98.packit.mock.impl;

import com.jsbro98.packit.mock.api.ChatEngine;
import com.jsbro98.packit.mock.api.MessageListener;
import com.jsbro98.packit.mock.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
// @Profile("dev") enable when implementation is made
public class MockChatEngine implements ChatEngine {
  private final List<MessageListener> listeners;

  MockChatEngine() {
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
