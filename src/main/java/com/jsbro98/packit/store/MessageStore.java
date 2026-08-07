package com.jsbro98.packit.store;

import com.jsbro98.packit.model.ChatMessage;

import java.util.List;

public interface MessageStore {
  void saveMessage(ChatMessage message);

  List<ChatMessage> getMessages();
}
