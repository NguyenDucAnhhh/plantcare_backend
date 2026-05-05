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

    // Giá»¯ khÃ³a ngoáº¡i biáº¿t Ai lÃ  ngÆ°á»i gá»­i táº¥m áº£nh lÃ¡ cÃ¢y nÃ y truy váº¥n lÃªn Google Gemini
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // KhÆ°u láº¡i bá»©c áº£nh Ráº­p KhuÃ´n Gá»‘c (Thumbnail) Ä‘á»ƒ Admin tiá»‡n xoi xÃ©t Camera (Chá»©c nÄƒng UC47 Admin)
    @Column(nullable = false)
    private String imageUrl;

    // Tên cây / Giống loài cây (AI nhận diện)
    private String plantName;

    // TÃªn bá»‡nh mÃ  AI rÃºt trÃ­ch Ä‘Æ°a ra (VÃ­ dá»¥: "Bá»‡nh Äá»‘m Äen", "ChÃ¡y Bia LÃ¡")
    private String diseaseName;

    // NguyÃªn NhÃ¢n
    @Column(columnDefinition = "TEXT")
    private String cause;

    // CÃ¡ch chá»¯a
    @Column(columnDefinition = "TEXT")
    private String treatment;

    // Trá»ng sá»‘ Ä‘Ã¡nh giÃ¡ má»©c Ä‘á»™ cháº¯c cháº¯n cá»§a AI (Tá»· lá»‡ %) - TÃ¹y thuá»™c náº¿u API Gemini tráº£ vá» con sá»‘ nÃ y.
    private double confidenceScore;

    // Ráº¥t quan trá»ng Ä‘á»ƒ Admin biáº¿t NgÆ°á»i dÃ¹ng táº£i áº£nh giá» Dáº§n hay giá» TÃ½
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
