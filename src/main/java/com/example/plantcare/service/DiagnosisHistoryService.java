package com.example.plantcare.service;

import com.example.plantcare.dto.request.DiagnosisEvaluateRequest;
import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.request.DiagnosisRateRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DiagnosisHistoryService {
    DiagnosisHistoryResponse saveDiagnosis(DiagnosisHistoryRequest request, String email);
    List<DiagnosisHistoryResponse> getMyHistories(String email);
    
    DiagnosisHistoryResponse rateDiagnosis(Long id, DiagnosisRateRequest request, String email);
    DiagnosisHistoryResponse evaluateDiagnosis(Long id, DiagnosisEvaluateRequest request);
    Page<DiagnosisHistoryResponse> getAllDiagnoses(String status, Integer rating, String confidence, String search, Pageable pageable);
}
