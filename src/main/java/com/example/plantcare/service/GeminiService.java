package com.example.plantcare.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

/**
 * TANG SERVICE - GOOGLE GEMINI AI
 * ================================
 * - Inject WebClient.Builder tu WebClientConfig.
 * - Doc API Key va URL tu application.properties bang @Value.
 * - Ham analyzeImage(): Gui anh (base64) kem cau lenh (prompt) toi Gemini,
 *   nhan ve van ban phan tich benh cay.
 *
 * HOW IT WORKS (Luong chay thuc te):
 *   Flutter gui anh la cay len Spring Boot (/api/diagnosis/analyze)
 *   -> GeminiService nhan anh, ma hoa sang Base64
 *   -> Goi API Gemini: "Day la anh la cay, hay phan tich benh"
 *   -> Gemini tra ve JSON chua: "Benh dom den, do nam, cach chua..."
 *   -> Spring Boot trich xuat va tra ket qua ve cho Flutter
 *   -> Flutter hien thi Toa thuoc len man hinh
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final WebClient.Builder webClientBuilder;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.api-url}")
    private String apiUrl;

    /**
     * Gui anh la cay (base64) toi Gemini de phan tich benh.
     * @param imageBase64  Anh duoc ma hoa sang chuoi Base64
     * @param mimeType     Dinh dang anh (vi du: "image/jpeg", "image/png")
     * @return Chuoi van ban phan tich tu AI Gemini
     */
    public String analyzeImage(String imageBase64, String mimeType) {
        // Cau truc JSON gui toi Gemini API (theo tai lieu chinh thuc cua Google)
        String prompt = "Bạn là một chuyên gia thực vật học và bác sĩ cây trồng. Hãy phân tích hình ảnh cây trồng được cung cấp. " +
            "Xác định tên loài cây (tên giống/loài nếu có thể), xác định xem cây có đang mắc bệnh hay gặp vấn đề sức khỏe nào không, " +
            "đưa ra phương pháp điều trị đề xuất, và cho biết mức độ tin cậy của bạn (Cao, Trung bình, Thấp). " +
            "Bạn BẮT BUỘC phải trả về kết quả CHÍNH XÁC dưới dạng một đối tượng JSON, không kèm theo bất kỳ văn bản thừa hay định dạng markdown nào. " +
            "Chuỗi JSON phải có chính xác các khóa (keys) sau: 'plantName', 'diseaseName', 'cause', 'treatment', 'confidence'. " +
            "Nếu hình ảnh không phải là thực vật, hãy trả về 'Unknown' cho 'plantName' và giải thích lý do trong 'diseaseName'. " +
            "Nếu cây hoàn toàn khỏe mạnh, hãy điền 'None' hoặc 'Healthy' vào 'diseaseName'. Phần 'treatment' (cách chữa trị) cần rõ ràng và súc tích. " +
            "Lưu ý: Đối với 'confidence', hãy trả về một con số từ 0 đến 100 (ví dụ: 85.0). Đối với 'cause', hãy nêu ngắn gọn nguyên nhân gây bệnh.";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(
                                Map.of("text", prompt),
                                Map.of("inline_data", Map.of(
                                        "mime_type", mimeType,
                                        "data", imageBase64
                                ))
                        )
                ))
        );

        try {
            // Goi API Gemini va lay ve chuoi text ket qua
            Map<?, ?> response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl + "?key=" + apiKey) // Gan API Key vao URL
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // block() = cho ket qua tra ve (dong bo)

            // Trich xuat van ban tu cau truc JSON phan hoi cua Gemini
            List<?> candidates = (List<?>) response.get("candidates");
            Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
            Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
            List<?> parts = (List<?>) content.get("parts");
            Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);

            return (String) firstPart.get("text");

        } catch (Exception e) {
            log.error("[Gemini] Loi goi API: {}", e.getMessage());
            return "Khong the phan tich anh luc nay. Vui long thu lai sau.";
        }
    }
}
