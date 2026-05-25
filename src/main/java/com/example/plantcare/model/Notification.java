package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông báo gửi tới cho máy nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User recipient;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    // Khớp Đặc Tả Phân Luồng: POST, FOLLOWER, REMINDER, SYSTEM
    @Column(nullable = false)
    private String type;

    // Chứa ID của Thực thể đích đễ App Tự Động Điều Hướng 
    // (Ví dụ: ID Bài đăng, ID User Theo dõi)
    private Long targetId;

    // Khớp Đặc Tả "Chấm đỏ biến mất" (Chưa đọc/Đã đọc)
    @Column(nullable = false)
    private boolean isRead = false;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
