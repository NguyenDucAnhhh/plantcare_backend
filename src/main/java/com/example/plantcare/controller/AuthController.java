package com.example.plantcare.controller;

import com.example.plantcare.dto.request.AuthRequest;
import com.example.plantcare.dto.request.RegisterRequest;
import com.example.plantcare.dto.response.AuthResponse;
import com.example.plantcare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Đánh Dấu Là Trạm Thu Phát Sóng Frontend (JSON)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    // Tinh Hoa Đặc Quyền (Injection) của Interface: Nó không cần biết AuthServiceImpl viết cái gì bên trong, nó chỉ nắm Đầu Mối là AuthService Interface.
    private final AuthService authService;

    // UC: Đăng Ký Hệ Thống
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Gọi thẳng Service để quăng cục nợ Logic đi. Vô Cùng Sạch sẽ!
        return ResponseEntity.ok(authService.register(request));
    }

    // UC 01: Đăng Nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
