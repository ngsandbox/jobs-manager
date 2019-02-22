package org.jobs.manager.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Slf4j
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.jobs.manager.backend")
public class TestBackendConfiguration {
}
