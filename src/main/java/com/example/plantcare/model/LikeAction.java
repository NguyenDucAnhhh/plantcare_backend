package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
// NGÄ‚N CHáº¶N HACKER SPAM TIM:
// Cáº¥u trÃºc RÃ o cáº¯m Unique nÃ y Ã©p há»‡ thá»‘ng PostgreSQL chá»‰ cho phÃ©p
// 1 ID User Ä‘Æ°á»£c phÃ©p ghÃ©p Ä‘Ã´i Like má»™t ID Post Ä‘Ãºng DUY NHáº¤T 1 Láº¦N!
// Náº¿u cá»‘ tÃ¬nh nÃ©m thÃªm Tim láº§n 2, SQL sáº½ bÃ¡o Lá»—i Ä‘Ã¡nh báº­t ngay láº­p tá»©c.
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

    // Ai Tháº£?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    // Tháº£ vÃ o BÃ i nÃ o?
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
