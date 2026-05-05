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

    // PHÃ“NG Káº¾T Ná»I DB CHO Báº¢O Vá»† SECURITY:
    // Cung cáº¥p dá»‹ch vá»¥ mÃ³c Email lÃªn thÃ nh cá»¥c Tháº» CÄƒn CÆ°á»›c UserDetails
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("KhÃ´ng tÃ¬m tháº¥y Email nÃ y!"));
    }

    // NHÃ€ MÃY Há»¢P NHáº¤T DAO (Data Access Object):
    // Ã‰p Password GÃµ tay + Password DB (BCrypt) Ä‘áº­p vÃ o nhau xem cÃ³ GÃ£y khÃ´ng?
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Láº¯p kÃ­nh LÃºp soi mÃ£ hÃ³a
        return authProvider;
    }

    // Tá»”NG QUáº¢N XÃšC Xáº®C ÄÄ‚NG NHáº¬P
    // Call cÃ¡i nÃ y á»Ÿ táº§ng Controller khi ngÆ°á»i dÃ¹ng nháº¥n nÃºt Login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // MÃY SAY THá»ŠT SINH RA THUáº¬T TOÃN HASH BCRYPT
    // Password cá»§a ngÆ°á»i dÃ¹ng sáº½ bá»‹ bÄƒm nÃ¡t thÃ nh chuá»—i Random trÆ°á»›c khi cÆ°a vÃ o DB PostgreSQL
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
