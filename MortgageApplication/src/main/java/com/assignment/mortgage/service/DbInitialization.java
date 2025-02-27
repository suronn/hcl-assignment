package com.assignment.mortgage.service;

import com.assignment.mortgage.config.MortgageConfiguration;
import com.assignment.mortgage.jpa.MortgageRate;
import com.assignment.mortgage.repository.MortgageRateRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DbInitialization {

    private final MortgageConfiguration mortgageConfiguration;
    private final MortgageRateRepository mortgageRateRepository;

    private List<MortgageRate> currentMortgageRates;

    @PostConstruct
    public void populateDatabase() {
        currentMortgageRates = new ArrayList<>();

        for (MortgageConfiguration.MortgageRateConfig rateConfig : mortgageConfiguration.getRates()) {
            currentMortgageRates.add(new MortgageRate(
                    rateConfig.getMaturityPeriod(),
                    BigDecimal.valueOf(rateConfig.getInterestRate()),
                    LocalDateTime.now()
            ));
        }
        mortgageRateRepository.saveAll(currentMortgageRates);
    }
}
