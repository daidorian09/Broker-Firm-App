package com.brokage.firm.application.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PasswordConfig.class);

    @Test
    void shouldProvideBCryptPasswordEncoderBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(PasswordEncoder.class);
            PasswordEncoder encoder = context.getBean(PasswordEncoder.class);
            assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);

            String rawPassword = "secret123";
            String encoded = encoder.encode(rawPassword);
            assertThat(encoder.matches(rawPassword, encoded)).isTrue();
        });
    }
}
