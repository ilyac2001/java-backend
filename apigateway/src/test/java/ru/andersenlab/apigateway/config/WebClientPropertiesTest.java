package ru.andersenlab.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "webclient.base-url=http://test-url.com"
})
class WebClientPropertiesTest {

    @Autowired
    private WebClientProperties webClientProperties;

    @Test
    void shouldLoadBaseUrlFromProperties() {
        assertThat(webClientProperties).isNotNull();
        assertThat(webClientProperties.getBaseUrl()).isEqualTo("http://test-url.com");
    }
}
