package com.example.plantcare.controller;

import com.example.plantcare.dto.request.AuthRequest;
import com.example.plantcare.dto.request.RegisterRequest;
import com.example.plantcare.dto.request.ForgotPasswordRequest;
import com.example.plantcare.dto.request.VerifyOtpRequest;
import com.example.plantcare.dto.request.ResetPasswordRequest;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<java.util.Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP đã được gửi đến email của bạn!"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<java.util.Map<String, String>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(java.util.Map.of("message", "OTP hợp lệ!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<java.util.Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(java.util.Map.of("message", "Đặt lại mật khẩu thành công!"));
    }
}