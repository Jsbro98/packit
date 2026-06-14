package com.jsbro98.packit.mock.api;

import com.jsbro98.packit.mock.model.Message;

public interface ChatEngine {
  void sendMessage(Message message);

  void onMessageReceived(MessageListener message);
}
