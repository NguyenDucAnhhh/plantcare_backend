package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class DiagnosisHistoryRequest {
    private String imageUrl;
    private String plantName;
    private String diseaseName;
    private String cause;
    private String treatment;
    private double confidenceScore;
}
