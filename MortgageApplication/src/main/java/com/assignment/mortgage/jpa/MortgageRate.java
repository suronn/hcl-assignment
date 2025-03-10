package com.assignment.mortgage.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
public class MortgageRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private int maturityPeriod;
    private BigDecimal interestRate;
    private LocalDateTime lastUpdate;

    public MortgageRate(int maturityPeriod, BigDecimal interestRate, LocalDateTime lastUpdate) {
        this.maturityPeriod = maturityPeriod;
        this.interestRate = interestRate;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MortgageRate other)) return false;
        return Objects.equals(id, other.id)
                && maturityPeriod == other.maturityPeriod
                && interestRate.equals(other.interestRate) && lastUpdate.equals(other.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, maturityPeriod, interestRate);
    }
}
