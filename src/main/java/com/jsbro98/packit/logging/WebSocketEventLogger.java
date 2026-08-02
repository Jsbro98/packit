package com.jsbro98.packit.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

@Component
public class WebSocketEventLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventLogger.class);

  @EventListener
  public void onConnect(SessionConnectEvent event) {
    StompHeaderAccessor accessor = wrapEvent(event);
    LOGGER.info("STOMP connect attempt: sessionId={}", accessor.getSessionId());
  }

  @EventListener
  public void onConnected(SessionConnectedEvent event) {
    StompHeaderAccessor accessor = wrapEvent(event);
    LOGGER.info("Client connected: sessionId={}, user={}",
            accessor.getSessionId(), accessor.getUser());
  }

  @EventListener
  public void onSubscribe(SessionSubscribeEvent event) {
    StompHeaderAccessor accessor = wrapEvent(event);
    LOGGER.debug("Client subscribed: sessionId={}, destination={}",
            accessor.getSessionId(), accessor.getDestination());
  }

  @EventListener
  public void onUnsubscribe(SessionUnsubscribeEvent event) {
    StompHeaderAccessor accessor = wrapEvent(event);
    LOGGER.debug("Client unsubscribed: sessionId={}, destination={}",
            accessor.getSessionId(), accessor.getDestination());
  }

  @EventListener
  public void onDisconnect(SessionDisconnectEvent event) {
    LOGGER.info("Client disconnected: sessionId={}, status={}",
            event.getSessionId(), event.getCloseStatus());
  }

  private StompHeaderAccessor wrapEvent(AbstractSubProtocolEvent event) {
    return StompHeaderAccessor.wrap(event.getMessage());
  }
}
