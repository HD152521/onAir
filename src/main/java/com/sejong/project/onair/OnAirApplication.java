package com.sejong.project.onair;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OnAirApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnAirApplication.class, args);
    }

}
