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

    // DÃ¹ng kiá»ƒu TEXT Ä‘á»ƒ cho phÃ©p viáº¿t bÃ i dÃ i lÃª thÃª khÃ´ng bá»‹ cáº¯t chá»¯
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl; // Báº¯t buá»™c hoáº·c khÃ´ng táº£i áº£nh tÃ¹y Mobile App

    // BÃ i Ä‘Äƒng nÃ y do Äáº¡i Hiá»‡p nÃ o Ä‘Äƒng?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User author;

    // Äáº¾M LÆ¯á»¢T TIM (Giáº£i thuáº­t Tá»‘i Æ¯u): 
    // TrÃ¡nh bá»‹ cháº­m/Lag server khi dÃ¹ng Lá»‡nh Count(*) 1 triá»‡u like.
    private int likeCount = 0; 

    // Cá»œ KIá»‚M DUYá»†T BÃ€I ÄÄ‚NG (DÃ nh cho UC: Kiá»ƒm duyá»‡t bÃ i cá»§a ADMIN)
    // Máº·c Ä‘á»‹nh Ä‘Äƒng lÃªn lÃ  true (Hiá»‡n). 
    // Náº¿u bá»‹ BÃ¡o CÃ¡o sai pháº¡m báº­y báº¡, Admin báº¥m "XÃ³a bÃ i Ä‘Äƒng", cá» nÃ y láº­t thÃ nh false (LuÃ´n áº¨n Ä‘i).
    @Column(nullable = false)
    private boolean isVisible = true;

    // Má»™t bÃ i viáº¿t cÃ³ hÃ ng ngÃ n BÃ¬nh luáº­n cháº¡y theo
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
