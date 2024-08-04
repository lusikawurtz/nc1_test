package com.nc1_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Nc1TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Nc1TestApplication.class, args);
    }

}