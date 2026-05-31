package com.example.plantcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class PlantcareApplication {

    @PostConstruct
    public void init() {
        // Đồng bộ múi giờ của máy chủ Render về giờ Việt Nam (UTC+7)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PlantcareApplication.class, args);
    }

}
