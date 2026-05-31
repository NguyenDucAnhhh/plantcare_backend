package com.example.plantcare.repository;
import com.example.plantcare.model.DiagnosisHistory;
import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface DiagnosisHistoryRepository extends JpaRepository<DiagnosisHistory, Long>, JpaSpecificationExecutor<DiagnosisHistory> {
    List<DiagnosisHistory> findByUserOrderByCreatedAtDesc(User user);
}
