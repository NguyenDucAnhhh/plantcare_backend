package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class DiagnosisEvaluateRequest {
    private Boolean isCorrect;
    private String adminNote;
}
