package com.jsbro98.packit.it;

import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.jsbro98.packit.TestingUtils.validRequest;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIT {
  @LocalServerPort
  private Integer port;

  private WebSocketStompClient stompClient;
  private StompSession stompSession;

  private String getWSPath() {
    return String.format("ws://localhost:%d/ws", port);
  }

  @BeforeEach
  void setUp() throws Exception {
    stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    stompClient.setMessageConverter(new JacksonJsonMessageConverter(MimeTypeUtils.APPLICATION_JSON));
    stompSession = stompClient.connectAsync(getWSPath(), new StompSessionHandlerAdapter() {
    }).get(1, SECONDS);
  }

  // TODO: Rename and Refactor this VVVVV
  @Test
  void verifyThatMessageRoutingWorks() throws Exception {
    BlockingQueue<ChatMessage> blockingQueue = new ArrayBlockingQueue<>(1);
    SendMessageRequest request = validRequest();

    stompSession.subscribe("/topic/messages", new StompFrameHandler() {

      @Override
      @NonNull
      public Type getPayloadType(@NonNull StompHeaders headers) {
        return ChatMessage.class;
      }

      @Override
      public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        blockingQueue.add((ChatMessage) payload);
      }
    });

    stompSession.send("/app/send", request);

    ChatMessage message = blockingQueue.poll(3, SECONDS);

    assertAll(
            () -> assertNotNull(message),
            () -> assertEquals(request.sender(), message.sender()),
            () -> assertEquals(request.content(), message.content()));
  }
}
