package com.brokage.firm.application.configuration;

import com.brokage.firm.application.constant.SecurityConstant;
import com.brokage.firm.infrastructure.security.HeaderAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new HeaderAuthenticationFilter(), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.withUsername(SecurityConstant.BASIC_AUTH_USERNAME)
                .password(SecurityConstant.BASIC_AUTH_PASSWORD)
                .roles(SecurityConstant.USER_ROLE)
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}