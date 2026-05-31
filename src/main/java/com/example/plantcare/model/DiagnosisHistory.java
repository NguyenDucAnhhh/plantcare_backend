package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnosis_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    
    @Column(nullable = false)
    private String imageUrl;

    
    private String plantName;

    
    private String diseaseName;

    
    @Column(columnDefinition = "TEXT")
    private String cause;

    
    @Column(columnDefinition = "TEXT")
    private String treatment;

    
    private double confidenceScore;

    
    private LocalDateTime createdAt;

    
    @Column(name = "user_feedback_rating")
    private Integer userFeedbackRating = 0;

    
    @Column(name = "admin_is_correct")
    private Boolean adminIsCorrect;

    
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

