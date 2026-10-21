package com.jsbro98.packit.integration;

import com.jsbro98.packit.controller.StompControllerAdvice;
import com.jsbro98.packit.errors.InvalidMessageException;
import com.jsbro98.packit.model.SendMessageRequest;
import com.jsbro98.packit.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.util.MimeTypeUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

import static com.jsbro98.packit.TestingUtils.validRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
  NOTE: This class is annotated with comments I wrote
  as I will be referring to it when I need to test STOMP endpoints
 */

@SpringBootTest
class ChatControllerIT {

  @Autowired
  @Qualifier("clientInboundChannel")
  MessageChannel clientInboundChannel;

  @Autowired
  ObjectMapper mapper;

  @MockitoBean
  ChatService chatService;

  @MockitoSpyBean
  StompControllerAdvice advice;

  private void send(String destination, Object payload) throws Exception {
    // A StompHeaderAccessor is needed to create the frame to test the endpoint (think WebMVC testing)
    var headers = StompHeaderAccessor.create(StompCommand.SEND);
    headers.setDestination(destination); // matched against @MessageMapping
    headers.setContentType(MimeTypeUtils.APPLICATION_JSON); // optional here, but matches a real client; a wrong value breaks conversion
    // Session ID and attributes are NEEDED here.
    // Spring views either of these missing as an IllegalStateException as of 10/19 version Spring Boot::(v4.2.0-M2)
    headers.setSessionId("test-session");
    headers.setSessionAttributes(new HashMap<>());

    // take the DTO and use the ObjectMapper to give us a byte array
    byte[] body = mapper.writeValueAsBytes(payload);

    // clientInboundChannel can now use the byte[] and headers to send to our destination
    clientInboundChannel.send(MessageBuilder.createMessage(body, headers.getMessageHeaders()));
  }

  @Test
  void whenAValidMessageIsSent_chatControllerWillDelegate_toChatService() throws Exception {
    send("/app/send", validRequest());
    verify(chatService, timeout(1000)).processMessage(any(SendMessageRequest.class));
  }

  @Test
  void whenServiceThrowsInvalidMessageException_controllerRoutesItToOnInvalid() throws Exception {
    doThrow(InvalidMessageException.class).when(chatService).processMessage(any());

    send("/app/send", validRequest());

    verify(chatService, timeout(1000)).processMessage(any(SendMessageRequest.class));
    verify(advice, timeout(1000)).onInvalid(any(InvalidMessageException.class));
  }

  @Test
  void whenPayloadCannotBeConverted_controllerCatchesAndLogs_leavingServiceUntouched() throws Exception {
    send("/app/send", "{bad_message:");

    verify(advice, timeout(1000)).onFailedConversion(any(MessageConversionException.class));
    verify(chatService, never()).processMessage(any(SendMessageRequest.class));
  }
}