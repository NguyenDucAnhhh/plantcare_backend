package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dùng kiểu TEXT để cho phép viết bài dài lê thê không bị cắt chữ
    @Column(columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>(); // Bắt buộc hoặc không tải ảnh tùy Mobile App

    // Bài đăng này do Đại Hiệp nào đăng?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User author;

    // ĐẾM LƯỢT TIM (Giải thuật Tối Ưu): 
    // Tránh bị chậm/Lag server khi dùng Lệnh Count(*) 1 triệu like.
    private int likeCount = 0; 

    // CỜ KIỂM DUYỆT BÀI ĐĂNG (Dành cho UC: Kiểm duyệt bài của ADMIN)
    // Mặc định đăng lên là true (Hiện). 
    // Nếu bị Báo Cáo sai phạm bậy bạ, Admin bấm "Xóa bài đăng", cờ này lật thành false (Luôn Ẩn đi).
    @Column(nullable = false)
    private boolean isVisible = true;

    // Một bài viết có hàng ngàn Bình luận chạy theo
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
