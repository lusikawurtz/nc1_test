package com.nc1_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.nc1_test")
//@EntityScan("com.nc1_test")
public class Nc1TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(Nc1TestApplication.class, args);
    }

}