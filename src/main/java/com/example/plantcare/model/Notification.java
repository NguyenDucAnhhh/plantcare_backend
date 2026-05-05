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

    // ThÃ´ng bÃ¡o gá»­i tá»›i cho mÃ¡y nÃ o?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User recipient;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    // Khá»›p Äáº·c Táº£ PhÃ¢n Luá»“ng: POST, FOLLOWER, REMINDER, SYSTEM
    @Column(nullable = false)
    private String type;

    // Chá»©a ID cá»§a Thá»±c thá»ƒ Ä‘Ã­ch Ä‘á»… App Tá»± Äá»™ng Äiá»u HÆ°á»›ng 
    // (VÃ­ dá»¥: ID BÃ i Ä‘Äƒng, ID User Theo dÃµi)
    private Long targetId;

    // Khá»›p Äáº·c Táº£ "Cháº¥m Ä‘á» biáº¿n máº¥t" (ChÆ°a Ä‘á»c/ÄÃ£ Ä‘á»c)
    @Column(nullable = false)
    private boolean isRead = false;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
