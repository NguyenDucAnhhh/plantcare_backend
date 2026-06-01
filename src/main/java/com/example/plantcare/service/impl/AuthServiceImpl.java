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
import com.example.plantcare.dto.request.ForgotPasswordRequest;
import com.example.plantcare.dto.request.VerifyOtpRequest;
import com.example.plantcare.dto.request.ResetPasswordRequest;
import com.example.plantcare.service.EmailService;
import java.util.Random;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new com.example.plantcare.exception.AppException("AUTH_EMAIL_EXISTS", "Email đã được sử dụng!");
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
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
                
        String jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .message("Đăng nhập thành công!")
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name()) // "USER" hoặc "ADMIN"
                .build();
    }
    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
        
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetOtpCode(otp);
        user.setOtpExpiration(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    @Override
    public boolean verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
        
        if (user.getResetOtpCode() == null || !user.getResetOtpCode().equals(request.getOtp())) {
            throw new com.example.plantcare.exception.AppException("AUTH_INVALID_OTP", "Mã OTP không hợp lệ!");
        }
        
        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new com.example.plantcare.exception.AppException("AUTH_OTP_EXPIRED", "Mã OTP đã hết hạn!");
        }
        
        return true;
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
                
        if (user.getResetOtpCode() == null || !user.getResetOtpCode().equals(request.getOtp())) {
            throw new com.example.plantcare.exception.AppException("AUTH_INVALID_OTP", "Mã OTP không hợp lệ!");
        }
        
        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(LocalDateTime.now())) {
            throw new com.example.plantcare.exception.AppException("AUTH_OTP_EXPIRED", "Mã OTP đã hết hạn!");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetOtpCode(null);
        user.setOtpExpiration(null);
        userRepository.save(user);
    }
}
