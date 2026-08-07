package com.jsbro98.packit.engine.api;

import com.jsbro98.packit.model.ChatMessage;

@FunctionalInterface
public interface MessageListener {
  void onMessage(ChatMessage message);
}
