package com.jsbro98.packit.service;

import com.jsbro98.packit.model.Message;
import com.jsbro98.packit.store.MessageStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {
  private final MessageStore messageHistory;

  public HistoryService(MessageStore messageHistory) {
    this.messageHistory = messageHistory;
  }


  public List<Message> getMessageData() {
    return messageHistory.getMessages();
  }
}
