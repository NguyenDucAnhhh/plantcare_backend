package com.example.plantcare.controller;

import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.request.DiagnosisRateRequest;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/diagnosis")
@RequiredArgsConstructor
@Tag(name = "10. Phòng Khám Trí Tuệ Nhân Tạo (AI)", description = "AI Diagnosis")
public class DiagnosisHistoryController {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisHistoryController.class);
    private final DiagnosisHistoryService diagnosisHistoryService;
    private final GeminiService geminiService;
    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<DiagnosisHistoryResponse> analyzeImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {

        String imageUrl = cloudinaryService.uploadImage(file, "diagnosis");
        String imageBase64 = Base64.getEncoder().encodeToString(file.getBytes());
        String mimeType = file.getContentType() != null ? file.getContentType() : "image/jpeg";

        String geminiResult = geminiService.analyzeImage(imageBase64, mimeType);
        log.info("Gemini Raw Response: \n{}", geminiResult);

        String cleanJson = geminiResult.replaceAll("`json", "").replaceAll("`", "").trim();
        ObjectMapper mapper = new ObjectMapper();
        
        DiagnosisHistoryRequest request = new DiagnosisHistoryRequest();
        request.setImageUrl(imageUrl);
        
        try {
            JsonNode jsonNode = mapper.readTree(cleanJson);
            request.setPlantName(jsonNode.has("plantName") ? jsonNode.get("plantName").asText() : "Unknown");
            request.setDiseaseName(jsonNode.has("diseaseName") ? jsonNode.get("diseaseName").asText() : "Unknown");
            request.setCause(jsonNode.has("cause") ? jsonNode.get("cause").asText() : "Unknown");
            request.setTreatment(jsonNode.has("treatment") ? jsonNode.get("treatment").asText() : "Unknown");
            request.setConfidenceScore(jsonNode.has("confidence") ? jsonNode.get("confidence").asDouble() : 75.0);
        } catch (Exception e) {
            log.error("JSON parse error: {}", e.getMessage());
            request.setPlantName("Unknown");
            request.setDiseaseName("JSON parse error");
            request.setCause("Raw text: " + cleanJson);
            request.setTreatment("Please retry");
            request.setConfidenceScore(0.0);
        }

        DiagnosisHistoryResponse response = diagnosisHistoryService.saveDiagnosis(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DiagnosisHistoryResponse> saveDiagnosis(
            @RequestBody DiagnosisHistoryRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(diagnosisHistoryService.saveDiagnosis(request, authentication.getName()));
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<DiagnosisHistoryResponse> rateDiagnosis(
            @PathVariable Long id,
            @RequestBody DiagnosisRateRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(diagnosisHistoryService.rateDiagnosis(id, request, authentication.getName()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<DiagnosisHistoryResponse>> getMyHistories(Authentication authentication) {
        return ResponseEntity.ok(diagnosisHistoryService.getMyHistories(authentication.getName()));
    }
}
