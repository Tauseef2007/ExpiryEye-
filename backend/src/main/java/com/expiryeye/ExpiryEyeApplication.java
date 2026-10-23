package com.expiryeye;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ExpiryEyeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpiryEyeApplication.class, args);
    }
}
