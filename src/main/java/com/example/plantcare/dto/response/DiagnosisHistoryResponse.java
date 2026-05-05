package com.example.plantcare.dto.response;

import com.example.plantcare.model.DiagnosisHistory;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DiagnosisHistoryResponse {
    private Long id;
    private String imageUrl;
    private String plantName;
    private String diseaseName;
    private String cause;
    private String treatment;
    private double confidenceScore;
    private LocalDateTime createdAt;

    public static DiagnosisHistoryResponse fromEntity(DiagnosisHistory history) {
        return DiagnosisHistoryResponse.builder()
                .id(history.getId())
                .imageUrl(history.getImageUrl())
                .plantName(history.getPlantName())
                .diseaseName(history.getDiseaseName())
                .cause(history.getCause())
                .treatment(history.getTreatment())
                .confidenceScore(history.getConfidenceScore())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
