package com.brokage.firm.application.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "broker.supported")
@Getter
@Setter
public class BrokerCurrencyProperties {
    private List<String> currencies = new ArrayList<>();
}
