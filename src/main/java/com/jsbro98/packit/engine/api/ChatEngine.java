package com.jsbro98.packit.engine.api;

import com.jsbro98.packit.model.ChatMessage;

public interface ChatEngine {
  boolean sendMessage(ChatMessage message);

  // listeners used here to compose behavior as needed
  // ex. add a listener for validation or logging before sending the actual message
  void registerListener(MessageListener listener);
}
