package com.example.plantcare.config;

import com.example.plantcare.model.Role;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
                System.out.println("==========================================================");
                System.out.println("✅ Đã tự động tạo tài khoản Admin mặc định:");
                System.out.println("   Tài khoản: " + adminEmail);
                System.out.println("   Mật khẩu: 123456");
                System.out.println("==========================================================");
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
                    System.out.println("✅ Đã tự động khôi phục quyền cho tài khoản Admin mặc định.");
                }
            }
        };
    }
}
