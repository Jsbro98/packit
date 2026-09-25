package com.jsbro98.packit.model;

public record SendMessageRequest(String sender, String content) {
  public static boolean isMessageRequestInvalid(SendMessageRequest request) {
    return request == null ||
            request.content() == null ||
            request.content().isEmpty() ||
            request.sender() == null ||
            request.sender().isEmpty();
  }
}
