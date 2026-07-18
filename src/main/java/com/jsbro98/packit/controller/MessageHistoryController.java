package com.jsbro98.packit.controller;

import com.jsbro98.packit.model.Message;
import com.jsbro98.packit.service.HistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageHistoryController {
  private final HistoryService historyService;

  public MessageHistoryController(HistoryService historyService) {
    this.historyService = historyService;
  }

  @GetMapping("/history")
  public List<Message> getHistory() {
    return historyService.getMessageData();
  }
}
