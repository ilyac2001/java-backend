package ru.andersenlab.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379"
})
class RedisPropertiesTest {

    @Autowired
    private RedisProperties redisProperties;

    @Test
    void shouldLoadRedisPropertiesFromApplicationYml() {
        assertThat(redisProperties).isNotNull();

        assertThat(redisProperties.getHost()).isEqualTo("localhost");
        assertThat(redisProperties.getPort()).isEqualTo(6379);
    }
}
