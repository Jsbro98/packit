package com.jsbro98.packit.unit.service;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.impl.SimpleChatEngine;
import com.jsbro98.packit.store.InMemoryMessageStore;
import com.jsbro98.packit.store.MessageStore;

class ChatServiceTest extends ServiceTestBase {
  @Override
  ChatEngine getChatEngine() {
    return new SimpleChatEngine();
  }

  @Override
  MessageStore getMessageStore() {
    return new InMemoryMessageStore();
  }
}