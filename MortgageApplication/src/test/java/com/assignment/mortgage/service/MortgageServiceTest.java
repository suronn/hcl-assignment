package com.assignment.mortgage.service;

import com.assignment.mortgage.config.MortgageConfiguration;
import com.assignment.mortgage.controller.dto.MortgageCheckRequest;
import com.assignment.mortgage.controller.dto.MortgageCheckResponse;
import com.assignment.mortgage.jpa.MortgageRate;
import com.assignment.mortgage.repository.MortgageRateRepository;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MortgageServiceTest {
    public static final String MAX_MULTIPLE_OF_INCOME = "4.0";
    @Mock
    private MortgageRateRepository mortgageRateRepository;

    @Mock
    private MortgageConfiguration mortgageConfiguration;

    @InjectMocks
    private MortgageServiceImpl mortgageService;

    public static Stream<Arguments> feasibleMortgageRequests() {
        return Stream.of(
                Arguments.of(Named.of("Loan value is less than the home value",
                                MortgageCheckRequest.builder()
                                        .income(new BigDecimal("100000"))
                                        .maturityPeriod(10)
                                        .loanValue(new BigDecimal("150000"))
                                        .homeValue(new BigDecimal("200000"))
                                        .build()),
                        BigDecimal.valueOf(1448.41)),
                Arguments.of(Named.of("Loan value is equal than the home value",
                                MortgageCheckRequest.builder()
                                        .income(new BigDecimal("100000"))
                                        .maturityPeriod(10)
                                        .loanValue(new BigDecimal("200000"))
                                        .homeValue(new BigDecimal("200000"))
                                        .build()),
                        BigDecimal.valueOf(1931.21)),
                Arguments.of(Named.of("Mortgage is less than 4 times the income",
                                MortgageCheckRequest.builder()
                                        .income(new BigDecimal("100000"))
                                        .maturityPeriod(10)
                                        .loanValue(new BigDecimal("150000"))
                                        .homeValue(new BigDecimal("200000"))
                                        .build()),
                        BigDecimal.valueOf(1448.41))
        );
    }

    public static Stream<Arguments> notFeasibleMortgageRequests() {
        return Stream.of(
                Arguments.of(Named.of("Loan value is greater than the home value and valid period",
                        new MortgageCheckRequestRecord(new BigDecimal("100000"), 10, new BigDecimal("200000"), new BigDecimal("150000"), true))),
                Arguments.of(Named.of("Mortgage is greater than 4 times the income and valid period",
                        new MortgageCheckRequestRecord(new BigDecimal("4000"), 10, new BigDecimal("150000"), new BigDecimal("200000"), true))),
                Arguments.of(Named.of("Valid Request but maturity period not found",
                        new MortgageCheckRequestRecord(new BigDecimal("100000"), 100, new BigDecimal("150000"), new BigDecimal("200000"), false)))
        );
    }

    @Test
    void testFetchMortgageRates() {
        List<MortgageRate> rates = List.of(new MortgageRate(20, new BigDecimal(MAX_MULTIPLE_OF_INCOME), LocalDateTime.now()));
        when(mortgageRateRepository.findAll()).thenReturn(rates);

        List<MortgageRate> result = mortgageService.getInterestRates();
        assertThat(result).isEqualTo(rates);
    }

    @ParameterizedTest
    @MethodSource("feasibleMortgageRequests")
    void testCheckMortgageFeasibility(MortgageCheckRequest request, BigDecimal expectedMonthlyCost) {
        // Given
        when(mortgageConfiguration.getMaxMultipleOfIncome()).thenReturn(new BigDecimal(MAX_MULTIPLE_OF_INCOME));
        when(mortgageRateRepository.findByMaturityPeriod(request.getMaturityPeriod()))
                .thenReturn(Optional.of(new MortgageRate(request.getMaturityPeriod(), new BigDecimal("3.0"), LocalDateTime.now())));

        // When
        MortgageCheckResponse response = mortgageService.checkMortgage(request);

        // Then
        assertThat(response.isFeasible()).isTrue();
        assertThat(response.getMonthlyCost()).isEqualTo(expectedMonthlyCost);
    }

    @ParameterizedTest
    @MethodSource("notFeasibleMortgageRequests")
    void testCheckMortgageNonFeasibility(MortgageCheckRequestRecord requestRecord) {
        // Given
        when(mortgageConfiguration.getMaxMultipleOfIncome()).thenReturn(new BigDecimal(MAX_MULTIPLE_OF_INCOME));
        if (requestRecord.isValidPeriod()) {
            lenient().when(mortgageRateRepository.findByMaturityPeriod(requestRecord.maturityPeriod()))
                    .thenReturn(Optional.of(new MortgageRate(requestRecord.maturityPeriod(), new BigDecimal("3.0"), LocalDateTime.now())));
        }

        // When
        MortgageCheckResponse response = mortgageService.checkMortgage(requestRecord.toMortgageCheckRequest());

        // Then
        assertThat(response.isFeasible()).isFalse();
        assertThat(response.getMonthlyCost()).isZero();
    }

    record MortgageCheckRequestRecord(BigDecimal income, int maturityPeriod, BigDecimal loanValue, BigDecimal homeValue,
                                      Boolean isValidPeriod) {
        public MortgageCheckRequest toMortgageCheckRequest() {
            return MortgageCheckRequest.builder()
                    .income(income)
                    .maturityPeriod(maturityPeriod)
                    .loanValue(loanValue)
                    .homeValue(homeValue)
                    .build();
        }
    }
}