package com.assignment.mortgage.controller;

import com.assignment.mortgage.controller.dto.MortgageCheckRequest;
import com.assignment.mortgage.controller.dto.MortgageCheckResponse;
import com.assignment.mortgage.jpa.MortgageRate;
import com.assignment.mortgage.service.MortgageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MortgageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MortgageService mortgageService;

    public static Stream<Arguments> invalidMortgageCheckRequests() {
        return Stream.of(
                Arguments.of(Named.of("Zero income",
                        MortgageCheckRequest.builder()
                                .income(new BigDecimal("0"))
                                .maturityPeriod(20)
                                .loanValue(new BigDecimal("150000"))
                                .homeValue(new BigDecimal("200000"))
                                .build())),
                Arguments.of(Named.of("Zero maturity period",
                        MortgageCheckRequest.builder()
                                .income(new BigDecimal("10000"))
                                .maturityPeriod(0)
                                .loanValue(new BigDecimal("150000"))
                                .homeValue(new BigDecimal("200000"))
                                .build())),
                Arguments.of(Named.of("Zero loan value",
                        MortgageCheckRequest.builder()
                                .income(new BigDecimal("10000"))
                                .maturityPeriod(20)
                                .loanValue(new BigDecimal("0"))
                                .homeValue(new BigDecimal("200000"))
                                .build())),
                Arguments.of(Named.of("Zero home value",
                        MortgageCheckRequest.builder()
                                .income(new BigDecimal("10000"))
                                .maturityPeriod(20)
                                .loanValue(new BigDecimal("150000"))
                                .homeValue(new BigDecimal("0"))
                                .build()))
        );
    }

    @Test
    void getInterestRates() throws Exception {
        // Given
        MortgageRate rate1 = new MortgageRate(10, new BigDecimal("0.05"), LocalDateTime.now());
        MortgageRate rate2 = new MortgageRate(10, new BigDecimal("0.03"), LocalDateTime.now());
        final var mortgageRates = Arrays.asList(rate1, rate2);
        when(mortgageService.getInterestRates()).thenReturn(mortgageRates);

        // When
        String response = mockMvc.perform(get("/api/interest-rates"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        List<MortgageRate> ratesInResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertThat(ratesInResponse)
                .isNotEmpty()
                .containsExactlyInAnyOrder(rate1, rate2);
    }

    @Test
    void shouldReturnSuccessfulFeasibility() throws Exception {
        // Given
        MortgageCheckRequest request = MortgageCheckRequest.builder()
                .income(new BigDecimal("50000"))
                .maturityPeriod(20)
                .loanValue(new BigDecimal("150000"))
                .homeValue(new BigDecimal("200000"))
                .build();
        String requestBody = objectMapper.writeValueAsString(request);
        MortgageCheckResponse response = MortgageCheckResponse.builder().feasible(true).monthlyCost(new BigDecimal("1000")).build();
        when(mortgageService.checkMortgage(any(MortgageCheckRequest.class))).thenReturn(response);

        // When
        String responseString = mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        MortgageCheckResponse mortgageResponse = objectMapper.readValue(responseString, MortgageCheckResponse.class);
        assertThat(mortgageResponse.isFeasible()).isTrue();
        assertThat(mortgageResponse.getMonthlyCost()).isPositive();
    }

    @Test
    void shouldReturnUnSuccessfulFeasibility() throws Exception {
        // Given
        MortgageCheckRequest request = MortgageCheckRequest.builder()
                .income(new BigDecimal("50000"))
                .maturityPeriod(20)
                .loanValue(new BigDecimal("15000"))
                .homeValue(new BigDecimal("200000"))
                .build();
        String requestBody = objectMapper.writeValueAsString(request);
        MortgageCheckResponse response = MortgageCheckResponse.builder().feasible(false).monthlyCost(BigDecimal.ZERO).build();
        when(mortgageService.checkMortgage(any(MortgageCheckRequest.class))).thenReturn(response);

        // When
        String responseString = mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        MortgageCheckResponse mortgageResponse = objectMapper.readValue(responseString, MortgageCheckResponse.class);
        assertThat(mortgageResponse.isFeasible()).isFalse();
        assertThat(mortgageResponse.getMonthlyCost()).isZero();
    }

    @Test
    void shouldReturnFeasibilityForExtraFields() throws Exception {
        // Given
        String requestBody = "{\"income\":50000,\"maturityPeriod\":20,\"loanValue\":150000,\"homeValue\":200000,\"extraField1\":10}";
        MortgageCheckResponse response = MortgageCheckResponse.builder().feasible(true).monthlyCost(new BigDecimal("1000")).build();
        when(mortgageService.checkMortgage(any(MortgageCheckRequest.class))).thenReturn(response);

        // When
        String responseString = mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        MortgageCheckResponse mortgageResponse = objectMapper.readValue(responseString, MortgageCheckResponse.class);
        assertThat(mortgageResponse.isFeasible()).isTrue();
        assertThat(mortgageResponse.getMonthlyCost()).isPositive();
    }

    @ParameterizedTest
    @MethodSource("invalidMortgageCheckRequests")
    void shouldValidateRequest(MortgageCheckRequest request) throws Exception {
        // Given
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        String responseString = mockMvc.perform(post("/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then
        MortgageCheckResponse mortgageResponse = objectMapper.readValue(responseString, MortgageCheckResponse.class);
        assertThat(mortgageResponse.isFeasible()).isFalse();
        assertThat(mortgageResponse.getMonthlyCost()).isNull();
    }
}