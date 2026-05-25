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

    // Giữ khóa ngoại biết Ai là người gửi tấm ảnh lá cây này truy vấn lên Google Gemini
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // Khưu lại bức ảnh Rập Khuôn Gốc (Thumbnail) để Admin tiện xoi xét Camera (Chức năng UC47 Admin)
    @Column(nullable = false)
    private String imageUrl;

    // T�n c�y / Giống lo�i c�y (AI nhận diện)
    private String plantName;

    // Tên bệnh mà AI rút trích đưa ra (Ví dụ: "Bệnh Đốm Đen", "Cháy Bia Lá")
    private String diseaseName;

    // Nguyên Nhân
    @Column(columnDefinition = "TEXT")
    private String cause;

    // Cách chữa
    @Column(columnDefinition = "TEXT")
    private String treatment;

    // Trọng số đánh giá mức độ chắc chắn của AI (Tỷ lệ %) - Tùy thuộc nếu API Gemini trả về con số này.
    private double confidenceScore;

    // Rất quan trọng để Admin biết Người dùng tải ảnh giờ Dần hay giờ Tý
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
