package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.DiagnosisEvaluateRequest;
import com.example.plantcare.dto.request.DiagnosisHistoryRequest;
import com.example.plantcare.dto.request.DiagnosisRateRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;
import com.example.plantcare.model.DiagnosisHistory;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.DiagnosisHistoryRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.DiagnosisHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiagnosisHistoryServiceImpl implements DiagnosisHistoryService {

    private final DiagnosisHistoryRepository diagnosisHistoryRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
    }

    @Override
    public DiagnosisHistoryResponse saveDiagnosis(DiagnosisHistoryRequest request, String email) {
        User user = getUserByEmail(email);

        DiagnosisHistory history = DiagnosisHistory.builder()
                .user(user)
                .imageUrl(request.getImageUrl())
                .plantName(request.getPlantName())
                .diseaseName(request.getDiseaseName())
                .cause(request.getCause())
                .treatment(request.getTreatment())
                .confidenceScore(request.getConfidenceScore())
                .userFeedbackRating(0)
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

    @Override
    public DiagnosisHistoryResponse rateDiagnosis(Long id, DiagnosisRateRequest request, String email) {
        DiagnosisHistory history = diagnosisHistoryRepository.findById(id)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("DIAGNOSIS_NOT_FOUND", "Kết quả chẩn đoán không tồn tại!"));
        

        history.setUserFeedbackRating(request.getRating());
        return DiagnosisHistoryResponse.fromEntity(diagnosisHistoryRepository.save(history));
    }

    @Override
    public DiagnosisHistoryResponse evaluateDiagnosis(Long id, DiagnosisEvaluateRequest request) {
        DiagnosisHistory history = diagnosisHistoryRepository.findById(id)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("DIAGNOSIS_NOT_FOUND", "Kết quả chẩn đoán không tồn tại!"));
        
        history.setAdminIsCorrect(request.getIsCorrect());
        history.setAdminNote(request.getAdminNote());
        return DiagnosisHistoryResponse.fromEntity(diagnosisHistoryRepository.save(history));
    }

    @Override
    public Page<DiagnosisHistoryResponse> getAllDiagnoses(String status, Integer rating, String confidence, String search, Pageable pageable) {
        Specification<DiagnosisHistory> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by Admin verification status
            if (status != null && !status.isEmpty() && !status.equals("all")) {
                if (status.equals("verified")) {
                    predicates.add(cb.isNotNull(root.get("adminIsCorrect")));
                } else if (status.equals("unverified")) {
                    predicates.add(cb.isNull(root.get("adminIsCorrect")));
                } else if (status.equals("correct")) {
                    predicates.add(cb.isTrue(root.get("adminIsCorrect")));
                } else if (status.equals("incorrect")) {
                    predicates.add(cb.isFalse(root.get("adminIsCorrect")));
                }
            }
            
            // Filter by User feedback rating
            if (rating != null) {
                predicates.add(cb.equal(root.get("userFeedbackRating"), rating));
            }
            
            // Filter by Confidence score
            if (confidence != null && !confidence.isEmpty() && !confidence.equals("all")) {
                if (confidence.equals("low")) {
                    predicates.add(cb.lessThan(root.get("confidenceScore"), 50.0));
                } else if (confidence.equals("medium")) {
                    predicates.add(cb.between(root.get("confidenceScore"), 50.0, 80.0));
                } else if (confidence.equals("high")) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("confidenceScore"), 80.0));
                }
            }
            
            // Search text
            if (search != null && !search.isEmpty()) {
                String searchLower = "%" + search.toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("plantName")), searchLower),
                        cb.like(cb.lower(root.get("diseaseName")), searchLower),
                        cb.like(cb.lower(root.join("user").get("email")), searchLower)
                );
                predicates.add(searchPredicate);
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return diagnosisHistoryRepository.findAll(spec, pageable).map(DiagnosisHistoryResponse::fromEntity);
    }
}

