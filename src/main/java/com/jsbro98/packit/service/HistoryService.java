package com.jsbro98.packit.service;

import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.store.MessageStore;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);

  private final MessageStore messageHistory;

  public HistoryService(MessageStore messageHistory) {
    this.messageHistory = messageHistory;
  }

  public List<ChatMessage> getMessageData() {
    LOGGER.debug("Retrieving messages");
    return messageHistory.getMessages();
  }
}
