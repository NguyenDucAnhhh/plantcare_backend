package com.example.plantcare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * TANG SERVICE - OPENWEATHERMAP
 * ==============================
 * - Inject WebClient.Builder tu WebClientConfig (dung chung voi GeminiService).
 * - Ham getWeather(): Nhan vi do / kinh do tu Flutter,
 *   goi OpenWeatherMap, tra ve nhiet do va thoi tiet hien tai.
 *
 * HOW IT WORKS:
 *   Flutter lay GPS (vi do, kinh do) -> Gui len /api/weather?lat=...&lon=...
 *   -> WeatherService goi OpenWeatherMap API
 *   -> Nhan ve: nhiet do, do am, mo ta thoi tiet
 *   -> Tra ve cho Flutter hien thi tren man hinh Home
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WebClient.Builder webClientBuilder;

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.api-url}")
    private String apiUrl;

    /**
     * Lay thong tin thoi tiet theo toa do GPS.
     * @param lat Vi do (Latitude)
     * @param lon Kinh do (Longitude)
     * @return Map chua thong tin thoi tiet (nhiet do, mo ta, do am)
     */
    public Map<?, ?> getWeather(double lat, double lon) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric&lang=vi")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("[Weather] Loi goi API: {}", e.getMessage());
            return Map.of("error", "Khong the lay thoi tiet luc nay.");
        }
    }
}
