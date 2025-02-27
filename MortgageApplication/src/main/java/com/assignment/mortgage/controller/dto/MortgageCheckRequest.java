package com.assignment.mortgage.controller.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class MortgageCheckRequest {
    @NotNull(message = "Income must not be null")
    @DecimalMin(value = "0.01", inclusive = false, message = "Income must be greater than zero")
    private BigDecimal income;

    @NotNull(message = "Maturity period must not be null")
    @Min(value = 1, message = "Maturity period must be greater than zero")
    private int maturityPeriod;

    @NotNull(message = "Loan value must not be null")
    @DecimalMin(value = "0.01", inclusive = false, message = "Loan value must be greater than zero")
    private BigDecimal loanValue;

    @NotNull(message = "Home value must not be null")
    @DecimalMin(value = "0.01", inclusive = false, message = "Home value must be greater than zero")
    private BigDecimal homeValue;
}
