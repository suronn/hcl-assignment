package com.assignment.mortgage.service;

import com.assignment.mortgage.config.MortgageConfiguration;
import com.assignment.mortgage.controller.dto.MortgageCheckRequest;
import com.assignment.mortgage.controller.dto.MortgageCheckResponse;
import com.assignment.mortgage.jpa.MortgageRate;
import com.assignment.mortgage.repository.MortgageRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class MortgageServiceImpl implements MortgageService {
    private final MortgageRateRepository mortgageRateRepository;
    private final MortgageConfiguration mortgageConfiguration;

    @Override
    public List<MortgageRate> getInterestRates() {
        return mortgageRateRepository.findAll();
    }

    @Override
    public MortgageCheckResponse checkMortgage(MortgageCheckRequest request) {
        BigDecimal maxLoanBasedOnIncome = request.getIncome().multiply(mortgageConfiguration.getMaxMultipleOfIncome());
        BigDecimal loanValue = request.getLoanValue();
        BigDecimal homeValue = request.getHomeValue();

        if (isLoanAmountInvalid(loanValue, maxLoanBasedOnIncome, homeValue))
            return MortgageCheckResponse.builder().feasible(false).monthlyCost(BigDecimal.ZERO).build();
        var mortgageRate = retrieveMortgageRate(request.getMaturityPeriod());
        if (mortgageRate.isEmpty()) {
            log.error("No mortgage rate found for maturity period {}", request.getMaturityPeriod());
            return MortgageCheckResponse.builder().feasible(false).monthlyCost(BigDecimal.ZERO).build();
        }
        BigDecimal monthlyCost = getMonthlyCost(request, mortgageRate.get());

        return MortgageCheckResponse.builder().feasible(true).monthlyCost(monthlyCost).build();
    }

    private static boolean isLoanAmountInvalid(BigDecimal loanValue, BigDecimal maxLoanBasedOnIncome, BigDecimal homeValue) {
        if (loanValue.compareTo(maxLoanBasedOnIncome) > 0) {
            log.error("Loan value {} exceeds four times the income {}", loanValue, maxLoanBasedOnIncome);
            return true;
        }

        if (loanValue.compareTo(homeValue) > 0) {
            log.error("Loan value {} exceeds home value {}", loanValue, homeValue);
            return true;
        }
        return false;
    }

    private Optional<MortgageRate> retrieveMortgageRate(int maturityPeriod) {
        return mortgageRateRepository.findByMaturityPeriod(maturityPeriod);
    }

    /**
     * Calculate the monthly cost of the mortgage
     * referecence: <a href="https://www.wikihow.com/Calculate-Mortgage-Payments#Calculating-Mortgage-Payments-with-an-Equation">Calculating-Mortgage-Payments-with-an-Equation</a>
     *
     * @param request mortgage check request
     * @param rate    mortgage rate
     * @return monthly cost of the mortgage
     */
    private static BigDecimal getMonthlyCost(MortgageCheckRequest request, MortgageRate rate) {

        // Convert annual interest rate to monthly and percentage to decimal
        BigDecimal monthlyInterestRate = BigDecimal.valueOf((rate.getInterestRate().doubleValue() / 100) / 12);
        // Total number of payments (months)
        int totalPayments = request.getMaturityPeriod() * 12;

        // Monthly payment formula
        // Calculate the monthly interest rate
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyInterestRate);
        BigDecimal powerFactor = onePlusRate.pow(totalPayments); // (1 + r)^n

        // Calculate the numerator and denominator
        BigDecimal numerator = request.getLoanValue().multiply(monthlyInterestRate).multiply(powerFactor);
        BigDecimal denominator = powerFactor.subtract(BigDecimal.ONE); // (1 + r)^n - 1

        // Calculate the monthly payment
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }
}
