package com.jsbro98.packit.mock.api;

import com.jsbro98.packit.mock.model.Message;

@FunctionalInterface
public interface MessageListener {
  void onMessage(Message message);
}
