package com.assignment.mortgage.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mortgage")
public class MortgageConfiguration {
    private List<MortgageRateConfig> rates;
    private BigDecimal maxMultipleOfIncome;

    @Getter
    @Setter
    public static class MortgageRateConfig {
        private int maturityPeriod;
        private double interestRate;
    }
}
