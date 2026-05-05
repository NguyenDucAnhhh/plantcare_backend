package com.example.plantcare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * TANG CONFIG - WEB CLIENT
 * ========================
 * - WebClient la cong cu HTTP hien dai cua Spring (thay the RestTemplate cu)
 * - Dung de goi HTTP ra ngoai toi: Gemini AI, OpenWeatherMap, ...
 * - @Bean WebClient.Builder: Spring se Inject cai nay vao bat cu Service nao can goi API ngoai.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
