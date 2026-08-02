package com.jsbro98.packit.unit;

import com.jsbro98.packit.model.Message;
import com.jsbro98.packit.store.InMemoryMessageStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryMessageStoreTest {

  private InMemoryMessageStore store;

  @BeforeEach
  void setUp_InMemoryMessageStore() {
    store = new InMemoryMessageStore();
  }

  @Test
  void shouldSaveMessages_whenGivenMessages() {
    fillMessages(store);
    List<Message> messages = store.getMessages();

    assertThat(messages).hasSize(10);
    assertThat(messages.getFirst().content()).isEqualTo("0");
    assertThat(messages.getLast().content()).isEqualTo("9");
  }

  @Test
  void getMessages_shouldReturnAValidListOfMessages() {
    fillMessages(store);
    List<Message> messages = store.getMessages();
    assertThat(messages)
            .isNotNull()
            .isNotEmpty()
            .hasSize(10);
  }

  private void fillMessages(InMemoryMessageStore store) {
    for (int i = 0; i < 10; i++) {
      store.saveMessage(new Message("Testing", String.valueOf(i)));
    }
  }
}