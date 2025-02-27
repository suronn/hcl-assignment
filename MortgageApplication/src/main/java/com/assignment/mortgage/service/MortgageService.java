package com.assignment.mortgage.service;

import com.assignment.mortgage.controller.dto.MortgageCheckRequest;
import com.assignment.mortgage.controller.dto.MortgageCheckResponse;
import com.assignment.mortgage.jpa.MortgageRate;

import java.util.List;

public interface MortgageService {

    List<MortgageRate> getInterestRates();

    MortgageCheckResponse checkMortgage(MortgageCheckRequest request) throws Exception;
}