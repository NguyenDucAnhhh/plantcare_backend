package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gardens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên khu vườn (VD: Vườn xương rồng)
    @Column(nullable = false)
    private String name;

    // Vị trí (VD: Ban công, Sân thượng)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description; // Phục vụ chức năng ghi chú mô tả vườn

    private String imageUrl; // Ảnh vườn cây

    // Mối quan hệ MANY_TO_ONE: Nhập Vườn vào hệ thống phải biết Vườn này của Ai.
    // Nhiều Vườn thuộc về 1 User.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude // Ngăn chặn vòng lặp in chữ đệ quy gây sập RAM
    private User user;

    // Mối quan hệ ONE_TO_MANY: 1 Vườn chứa Nhiều Cây.
    // Lệnh Tối Cao Đáng Sợ Bậc Nhất (cascade = CascadeType.ALL, orphanRemoval = true):
    // Phục vụ cho chức năng UC11 (Xóa Vườn)! Khi Vườn bị xóa, Postgres sẽ bắn lệnh chém sạch 
    // bay mầu toàn bộ cây trồng (Plant) bên trong cái vườn đó khỏi CSDL.
    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Plant> plants = new ArrayList<>();
}
