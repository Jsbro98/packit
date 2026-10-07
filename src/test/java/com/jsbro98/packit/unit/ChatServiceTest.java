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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChatServiceTest {
  private SimpMessagingTemplate template;
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
      template = mock(SimpMessagingTemplate.class);
      store = new InMemoryMessageStore();
      chatEngine = new SimpleChatEngine();
      chatService = new ChatService(chatEngine, store, template);
    }

    @Test
    void processMessage_whenGivenAValidMessage_shouldBroadcastAndSave() {
      chatService.processMessage(validRequest());

      verify(template).convertAndSend(eq("/topic/messages"), any(ChatMessage.class));
      assertThat(store.getMessages()).hasSize(1);
    }

    @Test
    void processMessage_whenInvalid_shouldNotSaveOrBroadcast() {
      assertThatThrownBy(() -> chatService.processMessage(invalidRequest()))
              .isInstanceOf(InvalidMessageException.class);

      assertThat(store.getMessages()).isEmpty();
      verifyNoInteractions(template);
    }
  }
}