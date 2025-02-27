package com.assignment.mortgage.repository;

import com.assignment.mortgage.jpa.MortgageRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MortgageRateRepository extends JpaRepository<MortgageRate, Long> {
    java.util.Optional<MortgageRate> findByMaturityPeriod(int maturityPeriod);
}