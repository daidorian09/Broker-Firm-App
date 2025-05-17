package com.brokage.firm.application.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Mock
    private BrokerApplicationConfig brokerConfig;

    @Mock
    private BrokerApplicationConfig.RedisConfig redisProps;

    @Test
    void shouldCreateRedissonClientWithCorrectAddress() {
        when(brokerConfig.getRedis()).thenReturn(redisProps);
        when(redisProps.getHost()).thenReturn("localhost");
        when(redisProps.getPort()).thenReturn(6379);

        final RedisConfig redisConfig = new RedisConfig(brokerConfig);

        final RedissonClient client = redisConfig.redissonClient();

        final Config config = client.getConfig();
        final String actualAddress = config.useSingleServer().getAddress();
        assertThat(actualAddress).isEqualTo("redis://localhost:6379");

        client.shutdown();
    }
}