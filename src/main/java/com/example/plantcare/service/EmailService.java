package com.example.plantcare.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class EmailService {

    public void sendOtpEmail(String to, String otp) {
        // Chạy ngầm (async) để API trả về kết quả ngay lập tức, không làm giật lag App Flutter
        CompletableFuture.runAsync(() -> {
            try {
                // Link Web App Google Apps Script của bạn
                String gasUrl = "https://script.google.com/macros/s/AKfycbyigU8o9o0nB29wI4tktk8IxHcagdsJ3-9Q5WKIc7AqxBG4scGe4z2FZTyZ_TL41Ipfyg/exec";
                
                String subject = "Mã OTP Đặt Lại Mật Khẩu - PlantCare";
                String htmlBody = "<p>Xin chào,</p><p>Bạn đã yêu cầu đặt lại mật khẩu. Mã OTP của bạn là: <strong style='font-size: 24px; color: #4CAF50;'>" + otp + "</strong></p><p>Mã này sẽ hết hạn trong vòng 10 phút.<br>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p><p>Trân trọng,<br>Đội ngũ PlantCare</p>";

                // Đóng gói thành chuỗi JSON
                String jsonBody = "{"
                        + "\"to\": \"" + to + "\","
                        + "\"subject\": \"" + subject + "\","
                        + "\"htmlBody\": \"" + htmlBody.replace("\"", "\\\"") + "\""
                        + "}";

                // Google Apps Script luôn trả về mã 302 Redirect, nên PHẢI bật followRedirects(NORMAL)
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(15))
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(gasUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() >= 200 && response.statusCode() < 400) {
                    log.info("Sent OTP email to: {} via Google Apps Script. Response: {}", to, response.body());
                } else {
                    log.error("Failed to send email to {}. Status code: {}, Response: {}", to, response.statusCode(), response.body());
                }
            } catch (Exception e) {
                log.error("Exception while sending email to {}: {}", to, e.getMessage());
            }
        });
    }
}
