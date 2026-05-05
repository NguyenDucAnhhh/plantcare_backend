package com.example.plantcare.controller;

import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;
import com.example.plantcare.service.CloudinaryService;
import com.example.plantcare.service.DiagnosisHistoryService;
import com.example.plantcare.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
@Tag(name = "10. Phòng Khám Trí Tuệ Nhân Tạo (AI)", description = "Lưu trữ lịch sử khám bệnh qua ảnh")
public class DiagnosisHistoryController {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisHistoryController.class);

    private final DiagnosisHistoryService diagnosisHistoryService;
    private final GeminiService geminiService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Phân tích ảnh lá cây bằng AI Gemini",
               description = "Nhận ảnh từ Flutter, upload lên Cloudinary, gửi sang Gemini phân tích, lưu lịch sử và trả kết quả")
    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<DiagnosisHistoryResponse> analyzeImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        // 1. Upload anh len Cloudinary (thu muc 'diagnosis')
        String imageUrl = cloudinaryService.uploadImage(file, "diagnosis");

        // 2. Ma hoa anh sang Base64 de gui cho Gemini
        String imageBase64 = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        // 3. Goi Gemini AI de phan tich benh
        String geminiResult = geminiService.analyzeImage(imageBase64, mimeType);
        log.info("Gemini Raw Response: \n{}", geminiResult);

        // 4. Parse ket qua tu Gemini (dang JSON)
        String cleanJson = geminiResult.replaceAll("```json", "").replaceAll("```", "").trim();
        ObjectMapper mapper = new ObjectMapper();
        
        DiagnosisHistoryRequest request = new DiagnosisHistoryRequest();
        request.setImageUrl(imageUrl);
        
        try {
            JsonNode jsonNode = mapper.readTree(cleanJson);
            request.setPlantName(jsonNode.has("plantName") ? jsonNode.get("plantName").asText() : "Không xác định");
            request.setDiseaseName(jsonNode.has("diseaseName") ? jsonNode.get("diseaseName").asText() : "Không xác định");
            request.setCause(jsonNode.has("cause") ? jsonNode.get("cause").asText() : "Không xác định");
            request.setTreatment(jsonNode.has("treatment") ? jsonNode.get("treatment").asText() : "Không xác định");
            request.setConfidenceScore(jsonNode.has("confidence") ? jsonNode.get("confidence").asDouble() : 75.0);
        } catch (Exception e) {
            log.error("Loi parse JSON tu Gemini: {}", e.getMessage());
            request.setPlantName("Không xác định");
            request.setDiseaseName("Lỗi phân tích JSON");
            request.setCause("Raw text: " + cleanJson);
            request.setTreatment("Vui lòng thử lại");
            request.setConfidenceScore(0.0);
        }

        // 5. Luu lich su va tra ket qua ve Flutter
        DiagnosisHistoryResponse response = diagnosisHistoryService.saveDiagnosis(request, authentication.getName());
        // Inject them raw text tu Gemini de Flutter co the hien thi day du
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Lưu lại toa thuốc từ AI Gemini", description = "App Flutter gọi API này sau khi nhận kết quả phân tích từ AI")
    @PostMapping
    public ResponseEntity<DiagnosisHistoryResponse> saveDiagnosis(
            @RequestBody DiagnosisHistoryRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(diagnosisHistoryService.saveDiagnosis(request, authentication.getName()));
    }

    @Operation(summary = "Xem lại sổ khám bệnh của tài khoản")
    @GetMapping("/history")
    public ResponseEntity<List<DiagnosisHistoryResponse>> getMyHistories(Authentication authentication) {
        return ResponseEntity.ok(diagnosisHistoryService.getMyHistories(authentication.getName()));
    }

}
