package com.jsbro98.packit.unit;

import com.jsbro98.packit.engine.api.ChatEngine;
import com.jsbro98.packit.engine.impl.SimpleChatEngine;
import com.jsbro98.packit.errors.InvalidMessageException;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.service.ChatService;
import com.jsbro98.packit.store.InMemoryMessageStore;
import com.jsbro98.packit.store.MessageStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ChatServiceTest {
  private MessageStore store;
  private ChatEngine chatEngine;
  private ChatService chatService;

  private SendMessageRequest validRequest() {
    return new SendMessageRequest("Bob", "test");
  }

  private SendMessageRequest invalidRequest() {
    return new SendMessageRequest(null, "");
  }

  // TODO: refactor this to subclass an abstract test class
  //       to make different service configs easier to test
  @Nested
  class ChatServiceSimpleInMemoryTests {
    @BeforeEach
    void setUp() {
      store = new InMemoryMessageStore();
      chatEngine = new SimpleChatEngine();
      chatService = new ChatService(chatEngine, store);
    }

    @Test
    void processMessage_whenGivenAValidMessage_shouldBroadcastAndSave() {
      chatEngine = spy(SimpleChatEngine.class);
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
}