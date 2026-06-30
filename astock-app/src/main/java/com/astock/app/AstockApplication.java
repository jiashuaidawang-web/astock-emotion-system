package com.astock.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.astock")
public class AstockApplication {
    public static void main(String[] args) {
        SpringApplication.run(AstockApplication.class, args);
    }
}
