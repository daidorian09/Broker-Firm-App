package com.brokage.firm.application.configuration;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private static final String REDIS_CONNECTION_FORMAT_KEY = "redis://%s:%d";
    private final BrokerApplicationConfig brokerConfig;

    @Bean
    public RedissonClient redissonClient() {
        final Config config = new Config();
        config.useSingleServer()
                .setAddress(REDIS_CONNECTION_FORMAT_KEY.formatted(brokerConfig.getRedis().getHost(), brokerConfig.getRedis().getPort()));
        return Redisson.create(config);
    }
}
