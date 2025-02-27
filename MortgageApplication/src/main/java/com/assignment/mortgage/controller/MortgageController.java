package com.assignment.mortgage.controller;

import com.assignment.mortgage.controller.dto.MortgageCheckRequest;
import com.assignment.mortgage.controller.dto.MortgageCheckResponse;
import com.assignment.mortgage.jpa.MortgageRate;
import com.assignment.mortgage.service.MortgageService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MortgageController {
    private final MortgageService mortgageService;

    public MortgageController(MortgageService mortgageService) {
        this.mortgageService = mortgageService;
    }

    @GetMapping("/interest-rates")
    public List<MortgageRate> getInterestRates() {
        return mortgageService.getInterestRates();
    }

    @PostMapping("/mortgage-check")
    public MortgageCheckResponse checkMortgage(@Valid @RequestBody MortgageCheckRequest request) throws Exception {
        return mortgageService.checkMortgage(request);
    }
}