package com.jsbro98.packit.mock.impl;

import com.jsbro98.packit.mock.api.ChatEngine;
import com.jsbro98.packit.mock.api.MessageListener;
import com.jsbro98.packit.mock.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MockChatEngine implements ChatEngine {
  private final ArrayList<MessageListener> listeners;

  MockChatEngine() {
    listeners = new ArrayList<>();
  }

  @Override
  public void sendMessage(Message message) {
    // Do something to send a message
  }

  @Override
  public void onMessageReceived(MessageListener message) {
    // Do something when we get a message
  }

  public void addListener(MessageListener messageListener) {
    if (listeners.contains(messageListener)) {
      return;
    }

    listeners.add(messageListener);
  }
}
