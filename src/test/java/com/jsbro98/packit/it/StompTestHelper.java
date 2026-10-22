package com.jsbro98.packit.it;

import com.jsbro98.packit.model.ChatMessage;
import com.jsbro98.packit.model.SendMessageRequest;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class StompTestHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(StompTestHelper.class);
  private BlockingQueue<ChatMessage> queue;
  private WebSocketStompClient stompClient;
  private StompSession stompSession;
  private String broadcastDestination;

  public StompTestHelper() {
    // this is a helper POJO, no need for args constructor
  }

  // configs StompTestHelper with defaults. can be expanded upon if needed
  public void init(int port) throws Exception {
    LOGGER.debug("init StompTestHelper");
    queue = new ArrayBlockingQueue<>(1);

    stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    stompClient.setMessageConverter(new JacksonJsonMessageConverter(MimeTypeUtils.APPLICATION_JSON));

    stompSession = stompClient.connectAsync(getWSPath(port), new StompSessionHandlerAdapter() {
    }).get(1, SECONDS);
    LOGGER.debug("init StompTestHelper completed");
  }

  public void setDestinationAndBroadcast(String subscribePath, String broadcastPath) {
    setBroadcastDestination(broadcastPath);
    sessionSubscribe(subscribePath);
  }

  public void sessionSubscribe(String destination) {
    LOGGER.debug("sessionSubscribe {}", destination);
    stompSession.subscribe(destination, getDefaultHandler());
  }

  public void sessionSend(SendMessageRequest message) {
    LOGGER.debug("sessionSend {}", message);
    stompSession.send(getBroadcastDestination(), message);
  }

  public void disconnectSession() {
    stompSession.disconnect();
  }

  public ChatMessage pollQueueWithTimeout(long timeout, TimeUnit unit) throws Exception {
    return queue.poll(timeout, unit);
  }

  // ===================
  // Getters and Setters
  // ===================

  public BlockingQueue<ChatMessage> getQueue() {
    return queue;
  }

  public void setQueue(BlockingQueue<ChatMessage> queue) {
    this.queue = queue;
  }

  public StompClientSupport getStompClient() {
    return stompClient;
  }

  public void setStompClient(WebSocketStompClient stompClient) {
    this.stompClient = stompClient;
  }

  public StompSession getStompSession() {
    return stompSession;
  }

  public void setStompSession(StompSession stompSession) {
    this.stompSession = stompSession;
  }

  public String getBroadcastDestination() {
    return broadcastDestination;
  }

  public void setBroadcastDestination(String path) {
    broadcastDestination = path;
  }

  // ===============
  // Private Helpers
  // ===============

  private String getWSPath(int port) {
    return String.format("ws://localhost:%d/ws", port);
  }

  private StompFrameHandler getDefaultHandler() {
    return new StompFrameHandler() {
      @Override
      @NonNull
      public Type getPayloadType(@NonNull StompHeaders headers) {
        return ChatMessage.class;
      }

      @Override
      public void handleFrame(@NonNull StompHeaders headers, Object payload) {
        queue.add((ChatMessage) payload);
      }
    };
  }
}
