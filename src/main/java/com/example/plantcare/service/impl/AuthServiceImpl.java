package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.AuthRequest;
import com.example.plantcare.dto.request.RegisterRequest;
import com.example.plantcare.dto.response.AuthResponse;
import com.example.plantcare.model.Role;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.security.JwtService;
import com.example.plantcare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.USER)
                .isActive(true)
                .build();

        userRepository.save(user);
        
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .message("Đăng ký thành công!")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name()) // "USER" hoặc "ADMIN"
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));
                
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .message("Đăng nhập thành công!")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name()) // "USER" hoặc "ADMIN"
                .build();
    }
}
