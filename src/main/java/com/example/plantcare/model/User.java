package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    
    // Đánh dấu đây là Khóa chính (Primary Key), tự động tăng (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;

    // Email không được để trống (nullable = false) và không được trùng nhau (unique = true)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    // Ảnh đại diện có thể để trống lúc mới đăng ký
    private String avatarUrl;

    // Giới thiệu ngắn về bản th�n
    @Column(length = 500)
    private String bio;

    // Mã hóa cột Role dưới dạng chữ (STRING) thay vì số học (ORDINAL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Cờ trạng thái: Dùng cho chức năng UC39 (Ban/Khóa tài khoản admin)
    // Tự động gán bằng true khi tạo mới User
    @Column(nullable = false)
    private boolean isActive = true;

    // Lưu lại bộ đếm thơi gian khi tài khoản được sinh ra
    private LocalDateTime createdAt;
    
    // Hàm này sẽ tự động chạy TRƯỚC KHI lưu cục Data này xuống PostgreSQL
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ==== ĐẶC TẢ UC QUÊN MẬT KHẨU ==== //
    private String resetOtpCode;
    private LocalDateTime otpExpiration;

    // ==== ĐẶC TẢ UC KHÔNG GIAN BẠN BÈ MXH (Theo dõi/Hủy theo dõi) ==== //
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    @ToString.Exclude
    
    private java.util.Set<User> following = new java.util.HashSet<>();

    // ==== ĐẶC TẢ CÀI ĐẶT HỆ THỐNG - THÔNG BÁO ==== //
    @Column(nullable = false)
    private boolean notifyAll = true;

    @Column(nullable = false)
    private boolean notifyCommunity = true;

    @Column(nullable = false)
    private boolean notifyReminder = true;

    @Column(nullable = false)
    private boolean notifySystem = true;

    private String fcmToken;

    // ==== MÓC NỐI PHÂN QUYỀN HỆ THỐNG SPRING SECURITY KINH ĐIỂN ==== //

    // Trả về tờ giấy phân quyền (ROLE_USER hay ROLE_ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Lấy chuỗi định danh lõi để chứng minh mày là ai (Dùng Email làm cột mốc)
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // KHÔNG CHO PHÉP ĐĂNG NHẬP (Account Locked) nếu cờ `isActive = false`
    // Tích hợp hoàn hảo sức mạnh của Đặc Tả Khóa Tài Khoản (UC 39)
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}