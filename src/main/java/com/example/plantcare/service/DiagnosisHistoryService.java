package com.example.plantcare.service;

import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;

import java.util.List;

public interface DiagnosisHistoryService {
    DiagnosisHistoryResponse saveDiagnosis(DiagnosisHistoryRequest request, String email);
    List<DiagnosisHistoryResponse> getMyHistories(String email);
}
