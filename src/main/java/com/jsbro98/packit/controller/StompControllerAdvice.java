package com.jsbro98.packit.controller;

import com.jsbro98.packit.errors.InvalidMessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice(assignableTypes = ChatController.class)
public class StompControllerAdvice {
  private static final Logger LOGGER = LoggerFactory.getLogger(StompControllerAdvice.class);

  // invalid user requests are expected. swallow and log from controller
  // InvalidMessageException is thrown on construction in ChatMessage
  @MessageExceptionHandler(InvalidMessageException.class)
  public void onInvalid(InvalidMessageException e) {
    LOGGER.warn("Invalid message request: {}", e.getMessage());
  }

  // as part of handling bad input, a malformed message could be attempted to
  // be converted. Logging and moving on if that's the case.
  @MessageExceptionHandler(MessageConversionException.class)
  public void onFailedConversion(MessageConversionException e) {
    LOGGER.warn("Message could not be converted: {}", e.getMessage());
  }
}
