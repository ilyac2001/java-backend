package ru.andersenlab.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final WebClientProperties webClientProperties;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl(webClientProperties.getBaseUrl())
                .build();
    }
}
