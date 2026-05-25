package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "plants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Tên định danh do người dùng tự đặt (Cây hoa hồng ngoại)

    private String species; // Loài sinh học (Rosa)

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả quá trình lớn lên của cây

    private String imageUrl; // Chứa link URL của Amazon S3/Cloudinary

    private LocalDate datePlanted; // Tích chọn ngày gieo hạt / trồng vào chậu

    // Mối quan hệ MANY_TO_ONE: Một Cây bắt buộc phải được đặt trong 1 Vườn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id", nullable = false)
    @ToString.Exclude
    private Garden garden;

    // ĐẶC TẢ "XÓA CÂY LÀ XÓA LUÔN LỊCH TRÌNH": 
    // Ràng buộc Cascade Delete đảm bảo DB triệt tiêu toàn bộ thư mục Reminder bên trong khi Cây bị Trảm.
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private java.util.List<Reminder> reminders = new java.util.ArrayList<>();
}
