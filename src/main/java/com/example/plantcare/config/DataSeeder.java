package com.example.plantcare.config;

import com.example.plantcare.model.Role;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@plantcare.com";
            
            java.util.Optional<User> optionalAdmin = userRepository.findByEmail(adminEmail);
            if (optionalAdmin.isEmpty()) {
                User admin = User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode("123456"))
                        .fullName("Quản trị viên Hệ thống")
                        .role(Role.ADMIN)
                        .isActive(true)
                        .notifyAll(true)
                        .notifyCommunity(true)
                        .notifyReminder(true)
                        .notifySystem(true)
                        .build();
                        
                userRepository.save(admin);
                log.info("Đã tự động tạo tài khoản Admin mặc định:");
                log.info("Tài khoản: {}", adminEmail);
                log.info("Mật khẩu: 123456");
            } else {
                User admin = optionalAdmin.get();
                boolean needsUpdate = false;
                if (!admin.isActive()) {
                    admin.setActive(true);
                    needsUpdate = true;
                }
                if (admin.getRole() != Role.ADMIN) {
                    admin.setRole(Role.ADMIN);
                    needsUpdate = true;
                }
                if (needsUpdate) {
                    userRepository.save(admin);
                    log.info("Đã tự động khôi phục quyền cho tài khoản Admin mặc định.");
                }
            }
        };
    }
}
