package com.example.loanagent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LoanAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanAgentApplication.class, args);
    }
}
