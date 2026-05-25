package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
// NGĂN CHẶN HACKER SPAM TIM:
// Cấu trúc Rào cắm Unique này ép hệ thống PostgreSQL chỉ cho phép
// 1 ID User được phép ghép đôi Like một ID Post đúng DUY NHẤT 1 LẦN!
// Nếu cố tình ném thêm Tim lần 2, SQL sẽ báo Lỗi đánh bật ngay lập tức.
@Table(name = "like_actions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "post_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ai Thả?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // Thả vào Bài nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
