package com.assignment.mortgage.controller.dto;

import lombok.Getter;

@Getter
public class MortgageCheckErrorResponse {
    private final String message;
    private final int status;
    private final long timestamp;

    public MortgageCheckErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}
