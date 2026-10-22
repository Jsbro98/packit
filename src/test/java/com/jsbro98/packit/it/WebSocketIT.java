package com.jsbro98.packit.it;

import com.jsbro98.packit.controller.StompControllerAdvice;
import com.jsbro98.packit.errors.InvalidMessageException;
import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.jsbro98.packit.TestingUtils.invalidRequest;
import static com.jsbro98.packit.TestingUtils.validRequest;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIT {
  private final StompTestHelper stompTestHelper = new StompTestHelper();

  @LocalServerPort
  private Integer port;

  @MockitoSpyBean
  private StompControllerAdvice controllerAdvice;

  @BeforeEach
  void setup() throws Exception {
    stompTestHelper.init(port);
    stompTestHelper.setDestinationAndBroadcast("/topic/messages", "/app/send");
  }

  @AfterEach
  void tearDown() {
    stompTestHelper.disconnectSession();
  }


  @Test
  void whenValidSendRequestSent_overStomp_deliversChatMessageToTopicSubscribers() throws Exception {
    SendMessageRequest request = validRequest();

    stompTestHelper.sessionSend(request);

    ChatMessage message = stompTestHelper.pollQueueWithTimeout(2, SECONDS);

    assertAll(
            () -> assertNotNull(message),
            () -> assertEquals(request.sender(), message.sender()),
            () -> assertEquals(request.content(), message.content()));
  }

  @Test
  void whenInvalidSendRequestSent_overStomp_ChatMessageDoesNotSend() throws Exception {
    SendMessageRequest request = invalidRequest();
    stompTestHelper.sessionSend(request);

    ChatMessage message = stompTestHelper.pollQueueWithTimeout(2, SECONDS);
    assertNull(message);
  }

  @Test
  void whenInvalidSendRequestSent_controllerAdviceCatchesCall() {
    SendMessageRequest request = invalidRequest();
    stompTestHelper.sessionSend(request);

    verify(controllerAdvice, timeout(2000)).onInvalid(any(InvalidMessageException.class));
  }
}
