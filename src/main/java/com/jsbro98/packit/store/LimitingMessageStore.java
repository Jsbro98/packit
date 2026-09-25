package com.jsbro98.packit.store;

public interface LimitingMessageStore extends MessageStore {
  int getMaxMessageLimit();
}
