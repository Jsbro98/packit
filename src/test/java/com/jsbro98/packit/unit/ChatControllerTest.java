package com.jsbro98.packit.unit;

import com.jsbro98.packit.controller.ChatController;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.service.ChatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  @Mock
  private ChatService chatService;

  @InjectMocks
  private ChatController controller;

  private SendMessageRequest validRequest() {
    return new SendMessageRequest("Test", "Test");
  }

  @Test
  void handleMessage_callsServiceOnce() {
    var request = validRequest();

    controller.handleMessage(request);

    verify(chatService).processMessage(request);
    verifyNoMoreInteractions(chatService);
  }

  @Test
  void handleMessage_passesSameInstanceThrough() {
    var request = validRequest();
    ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);

    controller.handleMessage(request);

    verify(chatService).processMessage(captor.capture());
    assertSame(request, captor.getValue());
  }
}