package com.example.plantcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PlantcareApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlantcareApplication.class, args);
    }

}
