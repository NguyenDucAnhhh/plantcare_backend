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
    
    // ÄÃ¡nh dáº¥u Ä‘Ã¢y lÃ  KhÃ³a chÃ­nh (Primary Key), tá»± Ä‘á»™ng tÄƒng (1, 2, 3...)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Email khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng (nullable = false) vÃ  khÃ´ng Ä‘Æ°á»£c trÃ¹ng nhau (unique = true)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    // áº¢nh Ä‘áº¡i diá»‡n cÃ³ thá»ƒ Ä‘á»ƒ trá»‘ng lÃºc má»›i Ä‘Äƒng kÃ½
    private String avatarUrl;

    // Giới thiệu ngắn về bản thân
    @Column(length = 500)
    private String bio;

    // MÃ£ hÃ³a cá»™t Role dÆ°á»›i dáº¡ng chá»¯ (STRING) thay vÃ¬ sá»‘ há»c (ORDINAL)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Cá» tráº¡ng thÃ¡i: DÃ¹ng cho chá»©c nÄƒng UC39 (Ban/KhÃ³a tÃ i khoáº£n admin)
    // Tá»± Ä‘á»™ng gÃ¡n báº±ng true khi táº¡o má»›i User
    @Column(nullable = false)
    private boolean isActive = true;

    // LÆ°u láº¡i bá»™ Ä‘áº¿m thÆ¡i gian khi tÃ i khoáº£n Ä‘Æ°á»£c sinh ra
    private LocalDateTime createdAt;
    
    // HÃ m nÃ y sáº½ tá»± Ä‘á»™ng cháº¡y TRÆ¯á»šC KHI lÆ°u cá»¥c Data nÃ y xuá»‘ng PostgreSQL
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ==== Äáº¶C Táº¢ UC QUÃŠN Máº¬T KHáº¨U ==== //
    private String resetOtpCode;
    private LocalDateTime otpExpiration;

    // ==== Äáº¶C Táº¢ UC KHÃ”NG GIAN Báº N BÃˆ MXH (Theo dÃµi/Há»§y theo dÃµi) ==== //
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_follows",
        joinColumns = @JoinColumn(name = "follower_id"),
        inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    @ToString.Exclude
    private java.util.Set<User> following = new java.util.HashSet<>();

    // ==== Äáº¶C Táº¢ CÃ€I Äáº¶T Há»† THá»NG - THÃ”NG BÃO ==== //
    @Column(nullable = false)
    private boolean notifyAll = true;

    @Column(nullable = false)
    private boolean notifyCommunity = true;

    @Column(nullable = false)
    private boolean notifyReminder = true;

    @Column(nullable = false)
    private boolean notifySystem = true;

    // ==== MÃ“C Ná»I PHÃ‚N QUYá»€N Há»† THá»NG SPRING SECURITY KINH ÄIá»‚N ==== //

    // Tráº£ vá» tá» giáº¥y phÃ¢n quyá»n (ROLE_USER hay ROLE_ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    // Láº¥y chuá»—i Ä‘á»‹nh danh lÃµi Ä‘á»ƒ chá»©ng minh mÃ y lÃ  ai (DÃ¹ng Email lÃ m cá»™t má»‘c)
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // KHÃ”NG CHO PHÃ‰P ÄÄ‚NG NHáº¬P (Account Locked) náº¿u cá» `isActive = false`
    // TÃ­ch há»£p hoÃ n háº£o sá»©c máº¡nh cá»§a Äáº·c Táº£ KhÃ³a TÃ i Khoáº£n (UC 39)
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
}
