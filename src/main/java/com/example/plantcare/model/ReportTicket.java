package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kẻ đi Cắm Cờ Phàn Nàn (Người báo cáo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @ToString.Exclude
    private User reporter;

    // Bài viết xấu số có hình đồi trụy bị nhắm mục tiêu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    // Lý do cắm cờ (Ví dụ: "Ảnh sexy quá không phải ảnh lá cây")
    @Column(nullable = false)
    private String reason;

    // 03 Trạng thái Kiểm duyệt giải quyết của Boss ADMIN (Khớp Use Case "Kiểm duyệt Bài Đăng")
    // - "PENDING": Báo láo, đang đợi check.
    // - "KEPT": Lời báo cáo tào lao, Admin quyết định Giữ nguyên Bài đăng này ("Chấp nhận bài đăng").
    // - "DELETED": Chuẩn ảnh bậy bạ, Admin đã Xóa Hủy Bỏ bài đăng.
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
