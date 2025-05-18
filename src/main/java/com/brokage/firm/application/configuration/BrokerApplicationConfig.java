package com.brokage.firm.application.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "broker.config")
@Getter
@Setter
public class BrokerApplicationConfig {
    private List<String> currencies = new ArrayList<>();
    private RedisConfig redis = new RedisConfig();
    private LockingTimeConfig lockingTime = new LockingTimeConfig();
    private SecurityConfig securityConfig = new SecurityConfig();

    @Getter
    @Setter
    public static class RedisConfig {
        private String host;
        private int port;
    }

    @Getter
    @Setter
    public static class LockingTimeConfig {
        private int waitTimeInSeconds;
        private int leaseTimeInSeconds;
    }

    @Getter
    @Setter
    public static class SecurityConfig {
        private String jwtSecretKey;
    }
}