package com.jsbro98.packit.store;

import com.jsbro98.packit.model.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryMessageStore implements MessageStore {
  private static final int MAX_MESSAGES = 50;

  // counter is needed as ConcurrentLinkedDeque is not O(1) for getting the size
  private final AtomicInteger counter = new AtomicInteger(0);
  private final ConcurrentLinkedDeque<Message> messages = new ConcurrentLinkedDeque<>();

  @Override
  public void saveMessage(Message message) {
    messages.addLast(message);

    if (counter.incrementAndGet() > MAX_MESSAGES) {
      messages.pollFirst();
      counter.decrementAndGet();
    }
  }

  @Override
  public List<Message> getMessages() {
    // avoiding returning null here
    if (messages.isEmpty()) {
      return Collections.emptyList();
    }

    var history = new ArrayList<>(messages);
    Collections.reverse(history);
    return history;
  }
}
