package com.marin.dulja.personalfinancetrackerbe;

import com.marin.dulja.personalfinancetrackerbe.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PersonalFinanceTrackerBEApplication {

    static void main(String[] args) {
        SpringApplication.run(PersonalFinanceTrackerBEApplication.class, args);
    }
}
