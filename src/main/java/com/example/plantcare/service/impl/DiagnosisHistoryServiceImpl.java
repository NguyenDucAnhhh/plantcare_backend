package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.DiagnosisHistory;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.DiagnosisHistoryRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.DiagnosisHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisHistoryServiceImpl implements DiagnosisHistoryService {

    private final DiagnosisHistoryRepository diagnosisHistoryRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public DiagnosisHistoryResponse saveDiagnosis(DiagnosisHistoryRequest request, String email) {
        User user = getUserByEmail(email);

        DiagnosisHistory history = DiagnosisHistory.builder()
                .user(user)
                .imageUrl(request.getImageUrl())
                .diseaseName(request.getDiseaseName())
                .cause(request.getCause())
                .treatment(request.getTreatment())
                .confidenceScore(request.getConfidenceScore())
                .build();

        return DiagnosisHistoryResponse.fromEntity(diagnosisHistoryRepository.save(history));
    }

    @Override
    public List<DiagnosisHistoryResponse> getMyHistories(String email) {
        User user = getUserByEmail(email);
        return diagnosisHistoryRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(DiagnosisHistoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
