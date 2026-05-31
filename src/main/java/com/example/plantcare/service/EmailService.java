package com.example.plantcare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Mã OTP Đặt Lại Mật Khẩu - PlantCare");
            message.setText("Xin chào,\n\nBạn đã yêu cầu đặt lại mật khẩu. Mã OTP của bạn là: " + otp + "\n\nMã này sẽ hết hạn trong vòng 5 phút.\nNếu bạn không yêu cầu, vui lòng bỏ qua email này.\n\nTrân trọng,\nĐội ngũ PlantCare");
            javaMailSender.send(message);
            log.info("Sent OTP email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
