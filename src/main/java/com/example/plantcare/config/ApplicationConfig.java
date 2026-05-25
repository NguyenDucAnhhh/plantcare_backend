package com.example.plantcare.config;

import com.example.plantcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    // PHÓNG KẾT NỐI DB CHO BẢO VỆ SECURITY:
    // Cung cấp dịch vụ móc Email lên thành cục Thẻ Căn Cước UserDetails
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy Email này!"));
    }

    // NHÀ MÁY HỢP NHẤT DAO (Data Access Object):
    // Ép Password Gõ tay + Password DB (BCrypt) đập vào nhau xem có Gãy không?
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Lắp kính Lúp soi mã hóa
        return authProvider;
    }

    // TỔNG QUẢN XÚC XẮC ĐĂNG NHẬP
    // Call cái này ở tầng Controller khi người dùng nhấn nút Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // MÁY SAY THỊT SINH RA THUẬT TOÁN HASH BCRYPT
    // Password của người dùng sẽ bị băm nát thành chuỗi Random trước khi cưa vào DB PostgreSQL
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
