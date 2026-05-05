package com.example.plantcare.service;

import com.example.plantcare.dto.request.AuthRequest;
import com.example.plantcare.dto.request.RegisterRequest;
import com.example.plantcare.dto.response.AuthResponse;

public interface AuthService {
    
    // Hàm Đăng Ký Hệ Thống
    AuthResponse register(RegisterRequest request);
    
    // Hàm Đăng Nhập
    AuthResponse login(AuthRequest request);
}
