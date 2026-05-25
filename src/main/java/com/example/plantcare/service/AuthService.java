package com.example.plantcare.service;

import com.example.plantcare.dto.request.AuthRequest;
import com.example.plantcare.dto.request.RegisterRequest;
import com.example.plantcare.dto.request.ForgotPasswordRequest;
import com.example.plantcare.dto.request.VerifyOtpRequest;
import com.example.plantcare.dto.request.ResetPasswordRequest;
import com.example.plantcare.dto.response.AuthResponse;

public interface AuthService {
    
    // Hàm Đăng Ký Hệ Thống
    AuthResponse register(RegisterRequest request);
    
    // Hàm Đăng Nhập
    AuthResponse login(AuthRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    boolean verifyOtp(VerifyOtpRequest request);
    void resetPassword(ResetPasswordRequest request);
}