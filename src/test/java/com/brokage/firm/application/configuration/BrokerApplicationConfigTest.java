package com.brokage.firm.application.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(BrokerApplicationConfig.class)
@TestPropertySource("classpath:application-test.properties")
class BrokerApplicationConfigTest {

    @Autowired
    private BrokerApplicationConfig config;

    @Test
    void shouldLoadPropertiesCorrectly() {
        assertThat(config.getCurrencies()).containsExactly("TRY", "USD");
        assertThat(config.getRedis().getHost()).isEqualTo("localhost");
        assertThat(config.getRedis().getPort()).isEqualTo(6379);
        assertThat(config.getLockingTime().getWaitTimeInSeconds()).isEqualTo(5);
        assertThat(config.getLockingTime().getLeaseTimeInSeconds()).isEqualTo(10);
    }
}
