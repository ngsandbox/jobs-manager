package org.jobs.manager.backend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@Configuration
@EnableWebSocketMessageBroker
public class WebFluxConfiguration implements WebFluxConfigurer {
}

