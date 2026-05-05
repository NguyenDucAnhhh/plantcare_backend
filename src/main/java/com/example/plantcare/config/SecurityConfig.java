package com.example.plantcare.config;

import com.example.plantcare.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Táº¯t cÆ¡ cháº¿ lá»«a Ä‘áº£o Session Ä‘Ã­nh kÃ¨m áº£o cáº¥m Hack CSRF (Giá»¯ láº¡i Code lÃµi Token Tháº­t)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Bat CORS voi cau hinh ben duoi
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // LUáº¬T Lá»† PHÃ‚N LÃ” Ä áº¤T Ná»€N: PHÃ‚N QUYá»€N TRUY Cáº¬P (Route Roles)
            .authorizeHttpRequests(auth -> auth
                
                // Má»ž CHá» T (PermitAll): Tráº¡m xÄƒng cÃ´ng cá»™ng, ai cháº£ cáº§n ghÃ© -> Má»Ÿ Sáº£nh Login vÃ  Register
                .requestMatchers("/api/auth/**").permitAll() 
                
                // Má»ž Cửa Cho Giao diện Tài Liệu Khách (Swagger UI)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/swagger-ui.html").permitAll()
                
                // VÃ™NG Cáº¤M Ä á»ŠA BAY (HAS_ROLE ADMIN): Chá»‰ ChÃºa Tá»ƒ má»›i Ä‘Æ°á»£c mÃ² máº«m vÃ o (VÃ­ dá»¥ URL giÃ¡m sÃ¡t AI)
                .requestMatchers("/api/admin/**").hasRole("ADMIN") 
                
                // Táº¥t cáº£ nhá»¯ng chá»— káº½ há»Ÿ khÃ¡c trong lÃ¢u Ä‘Ã i CÃ¢y Cáº£nh (VÆ°á» n, CÃ¢y, BÃ i Ä‘Äƒng...) 
                // Báº®T BUá»˜C Pháº£i xuáº¥t trÃ¬nh CÄƒn CÆ°á»›c JWT quáº¹t tháº»!
                .anyRequest().authenticated() 
            )
            
            // Láº¬P Lá»œI NGuyá» n Phi Tráº¡ng ThÃ¡i (Stateless): Server láº¥p nÃ£o pháº³ng, KhÃ´ng nhá»› Session cÅ©
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Ã‰P GIAO Ná»˜P RÃ€O CHáº®N
            .authenticationProvider(authenticationProvider)
            
            // QuÄƒng TÃªn LÃ­nh GÃ¡c Báº£o Vá»‡ (jwtAuthFilter) Ra Ä á»©ng Che Ngay Luá»“ng Cháº¡y Ä áº§u TiÃµn PhÃ­a MÃ´n Quan
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cau hinh CORS - Cho phep Flutter Web (Chrome) goi API
     * Khong co cai nay, trinh duyet se chon het moi request tu localhost
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Cho phep cac nguon goc nay goi API
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",   // Flutter Web (mac dinh)
            "http://localhost:8081",   // Flutter Web (port khac)
            "http://localhost:54321",  // Flutter Web (port ngau nhien)
            "http://127.0.0.1:3000"
        ));

        // Cho phep tat ca port localhost (flutter run -d chrome dung port ngau nhien)
        config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
