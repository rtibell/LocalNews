package com.tibell.integrations.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {
    @Bean
    public WebClient webSentimentClient(WebClient.Builder builder) {
        return builder
                .baseUrl("http://localhost:8090")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
