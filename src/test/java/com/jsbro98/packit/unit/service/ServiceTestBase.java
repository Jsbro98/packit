package com.jsbro98.packit.unit.service;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.errors.InvalidMessageException;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.service.ChatService;
import com.jsbro98.packit.store.MessageStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.jsbro98.packit.TestingUtils.invalidRequest;
import static com.jsbro98.packit.TestingUtils.validRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

abstract class ServiceTestBase {
  protected ChatService chatService;
  protected ChatEngine chatEngine;
  protected MessageStore store;

  abstract ChatEngine getChatEngine();

  abstract MessageStore getMessageStore();

  @BeforeEach
  void setUp() {
    chatEngine = getChatEngine();
    store = getMessageStore();
    chatService = new ChatService(chatEngine, store);
  }

  @Test
  void processMessage_whenGivenAValidMessage_shouldBroadcastAndSave() {
    chatEngine = spy(getChatEngine());
    chatService = new ChatService(chatEngine, store);

    chatService.processMessage(validRequest());

    verify(chatEngine).sendMessage(any(ChatMessage.class));
    assertThat(store.getMessages()).hasSize(1);
  }


  @Test
  void processMessage_whenInvalid_shouldNotSaveOrBroadcast() {
    assertThatThrownBy(() -> chatService.processMessage(invalidRequest()))
            .isInstanceOf(InvalidMessageException.class);

    assertThat(store.getMessages()).isEmpty();
  }
}
