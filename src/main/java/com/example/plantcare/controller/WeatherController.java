package com.example.plantcare.controller;

import com.example.plantcare.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Thời tiết", description = "Các API liên quan đến thông tin thời tiết")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "Lấy thông tin thời tiết hiện tại theo tọa độ")
    @GetMapping("/current")
    public ResponseEntity<Map<?, ?>> getCurrentWeather(@RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(weatherService.getWeather(lat, lon));
    }
}
