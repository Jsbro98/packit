package com.jsbro98.packit.model;

public sealed interface ChatPayload permits ChatMessage, SendMessageRequest {
  static boolean isInvalid(ChatPayload payload) {   // static, so null is a legal argument
    return payload == null
            || isBlank(payload.sender())
            || isBlank(payload.content());
  }

  private static boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  String sender();

  String content();
}
