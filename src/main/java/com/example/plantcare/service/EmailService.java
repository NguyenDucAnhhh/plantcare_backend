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
        // Chạy ngầm (async) để API trả về kết quả ngay lập tức, tránh bị timeout trên điện thoại
        CompletableFuture.runAsync(() -> {
            try {
                // API Key từ Resend
                String apiKey = "re_hAw5tUEi_mg1EeKBgmG3PSz6sDfoR7iD9";
                
                // Resend yêu cầu email gửi từ 'onboarding@resend.dev' đối với tài khoản miễn phí chưa verify domain
                String jsonBody = "{"
                        + "\"from\": \"onboarding@resend.dev\","
                        + "\"to\": [\"" + to + "\"],"
                        + "\"subject\": \"Mã OTP Đặt Lại Mật Khẩu - PlantCare\","
                        + "\"html\": \"<p>Xin chào,</p><p>Bạn đã yêu cầu đặt lại mật khẩu. Mã OTP của bạn là: <strong>" + otp + "</strong></p><p>Mã này sẽ hết hạn trong vòng 10 phút.<br>Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p><p>Trân trọng,<br>Đội ngũ PlantCare</p>\""
                        + "}";

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.resend.com/emails"))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    log.info("Sent OTP email to: {} via Resend API", to);
                } else {
                    log.error("Failed to send email to {}. Response: {}", to, response.body());
                }
            } catch (Exception e) {
                log.error("Exception while sending email to {}: {}", to, e.getMessage());
            }
        });
    }
}
