package com.example.plantcare.dto.request;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String otpCode;
    private String newPassword;
    private String confirmNewPassword;
}
