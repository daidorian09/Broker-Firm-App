package com.brokage.firm.application.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(BrokerApplicationConfig.class)
@TestPropertySource("classpath:application-test.properties")
class BrokerApplicationConfigTest {

    @Autowired
    private BrokerApplicationConfig config;

    @Test
    void shouldLoadPropertiesCorrectlyFromPropertyFile() {
        assertThat(config.getCurrencies()).containsExactly("TRY", "USD");
        assertThat(config.getRedis().getHost()).isEqualTo("localhost");
        assertThat(config.getRedis().getPort()).isEqualTo(6379);
        assertThat(config.getLockingTime().getWaitTimeInSeconds()).isEqualTo(5);
        assertThat(config.getLockingTime().getLeaseTimeInSeconds()).isEqualTo(10);
        assertThat(config.getSecurityConfig().getJwtSecretKey()).isEqualTo("test-key");
    }

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        // Given
        final BrokerApplicationConfig config = new BrokerApplicationConfig();

        final BrokerApplicationConfig.RedisConfig redisConfig = new BrokerApplicationConfig.RedisConfig();
        redisConfig.setHost("localhost");
        redisConfig.setPort(6379);

        final BrokerApplicationConfig.LockingTimeConfig lockConfig = new BrokerApplicationConfig.LockingTimeConfig();
        lockConfig.setWaitTimeInSeconds(5);
        lockConfig.setLeaseTimeInSeconds(10);

        final BrokerApplicationConfig.SecurityConfig securityConfig = new BrokerApplicationConfig.SecurityConfig();
        securityConfig.setJwtSecretKey("secret-key");

        final List<String> currencies = List.of("TRY", "USD");

        config.setCurrencies(currencies);
        config.setRedis(redisConfig);
        config.setLockingTime(lockConfig);
        config.setSecurityConfig(securityConfig);

        assertThat(config.getCurrencies()).containsExactly("TRY", "USD");
        assertThat(config.getRedis().getHost()).isEqualTo("localhost");
        assertThat(config.getRedis().getPort()).isEqualTo(6379);
        assertThat(config.getLockingTime().getWaitTimeInSeconds()).isEqualTo(5);
        assertThat(config.getLockingTime().getLeaseTimeInSeconds()).isEqualTo(10);
        assertThat(config.getSecurityConfig().getJwtSecretKey()).isEqualTo("secret-key");
    }
}