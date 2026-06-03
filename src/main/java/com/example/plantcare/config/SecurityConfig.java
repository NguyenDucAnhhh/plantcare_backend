package com.example.plantcare.config;

import com.example.plantcare.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Tắt cơ chế lừa đảo Session đính kèm ảo cấm Hack CSRF (Giữ lại Code lõi Token Thật)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Bat CORS voi cau hinh ben duoi
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // LUẬT LỆ PHÂN LÔ � ẤT NỀN: PHÂN QUYỀN TRUY CẬP (Route Roles)
            .authorizeHttpRequests(auth -> auth
                
                // MỞ CH� T (PermitAll): Trạm xăng công cộng, ai chả cần ghé -> Mở Sảnh Login và Register
                .requestMatchers("/api/auth/**").permitAll() 
                
                // MỞ Cửa Cho Giao diện T�i Liệu Kh�ch (Swagger UI)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/swagger-ui.html").permitAll()
                
                // VÙNG CẤM � ỊA BAY (HAS_ROLE ADMIN): Chỉ Chúa Tể mới được mò mẫm vào (Ví dụ URL giám sát AI)
                .requestMatchers("/api/admin/**").hasRole("ADMIN") 
                
                // Tất cả những chỗ kẽ hở khác trong lâu đài Cây Cảnh (Vư� n, Cây, Bài đăng...) 
                // BẮT BUỘC Phải xuất trình Căn Cước JWT quẹt thẻ!
                .anyRequest().authenticated() 
            )
            
            // LẬP LỜI NGuy� n Phi Trạng Thái (Stateless): Server lấp não phẳng, Không nhớ Session cũ
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // ÉP GIAO NỘP RÀO CHẮN
            .authenticationProvider(authenticationProvider)
            
            // Quăng Tên Lính Gác Bảo Vệ (jwtAuthFilter) Ra � ứng Che Ngay Luồng Chạy � ầu Tiõn Phía Môn Quan
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

        // Cho phep tat ca port localhost va moi domain (Firebase, Vercel...)
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
