package com.jsbro98.packit.engine.api;

import com.jsbro98.packit.model.Message;

@FunctionalInterface
public interface MessageListener {
  void onMessage(Message message);
}
