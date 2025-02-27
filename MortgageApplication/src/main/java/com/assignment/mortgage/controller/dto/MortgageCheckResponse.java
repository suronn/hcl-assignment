package com.assignment.mortgage.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class MortgageCheckResponse {
    @Builder.Default
    private boolean feasible = false;
    @Builder.Default
    private BigDecimal monthlyCost = BigDecimal.ZERO;
}
