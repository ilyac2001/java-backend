package ru.andersenlab.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "webclient.base-url=http://test-url.com"
})
class WebClientConfigTest {

    @Autowired
    private WebClient webClient;

    @Autowired
    private WebClientProperties webClientProperties;

    @Test
    void shouldCreateWebClientWithCorrectBaseUrl() {
        assertThat(webClient).isNotNull();

        String expectedBaseUrl = webClientProperties.getBaseUrl();
        assertThat(expectedBaseUrl).isEqualTo("http://test-url.com");
    }
}
