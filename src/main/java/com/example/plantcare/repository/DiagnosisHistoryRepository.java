package com.example.plantcare.repository;
import com.example.plantcare.model.DiagnosisHistory;
import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiagnosisHistoryRepository extends JpaRepository<DiagnosisHistory, Long> {
    List<DiagnosisHistory> findByUserOrderByCreatedAtDesc(User user);
}
