package com.jsbro98.packit.store;

import com.jsbro98.packit.model.Message;

import java.util.List;

public interface MessageStore {
  void saveMessage(Message message);

  List<Message> getMessages();
}
