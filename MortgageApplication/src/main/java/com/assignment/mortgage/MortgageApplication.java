package com.assignment.mortgage;

import com.assignment.mortgage.config.MortgageConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MortgageConfiguration.class)
public class MortgageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MortgageApplication.class, args);
    }
}
