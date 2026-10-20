package com.jsbro98.packit;

import com.jsbro98.packit.model.SendMessageRequest;

public class TestingUtils {
  public static SendMessageRequest invalidRequest() {
    return new SendMessageRequest(null, "");
  }

  public static SendMessageRequest validRequest() {
    return new SendMessageRequest("Test", "Test");
  }
}
