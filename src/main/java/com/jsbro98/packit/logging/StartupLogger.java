package com.jsbro98.packit.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupLogger {
  private static final Logger LOGGER = LoggerFactory.getLogger(StartupLogger.class);

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    String port = event.getApplicationContext().getEnvironment().getProperty("local.server.port");

    LOGGER.info("Starting PackIt on port: {}", port);
  }
}
