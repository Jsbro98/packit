package com.jsbro98.packit.errors;

public class ListenerFailedException extends RuntimeException {
  public ListenerFailedException(String message) {
    super(message);
  }

  public ListenerFailedException(String message, Throwable cause) {
    super(message, cause);
  }
}
