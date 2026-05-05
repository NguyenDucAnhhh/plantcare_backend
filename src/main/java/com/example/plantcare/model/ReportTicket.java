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

    // Káº» Ä‘i Cáº¯m Cá» PhÃ n NÃ n (NgÆ°á»i bÃ¡o cÃ¡o)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    @ToString.Exclude
    private User reporter;

    // BÃ i viáº¿t xáº¥u sá»‘ cÃ³ hÃ¬nh Ä‘á»“i trá»¥y bá»‹ nháº¯m má»¥c tiÃªu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    // LÃ½ do cáº¯m cá» (VÃ­ dá»¥: "áº¢nh sexy quÃ¡ khÃ´ng pháº£i áº£nh lÃ¡ cÃ¢y")
    @Column(nullable = false)
    private String reason;

    // 03 Tráº¡ng thÃ¡i Kiá»ƒm duyá»‡t giáº£i quyáº¿t cá»§a Boss ADMIN (Khá»›p Use Case "Kiá»ƒm duyá»‡t BÃ i ÄÄƒng")
    // - "PENDING": BÃ¡o lÃ¡o, Ä‘ang Ä‘á»£i check.
    // - "KEPT": Lá»i bÃ¡o cÃ¡o tÃ o lao, Admin quyáº¿t Ä‘á»‹nh Giá»¯ nguyÃªn BÃ i Ä‘Äƒng nÃ y ("Cháº¥p nháº­n bÃ i Ä‘Äƒng").
    // - "DELETED": Chuáº©n áº£nh báº­y báº¡, Admin Ä‘Ã£ XÃ³a Há»§y Bá» bÃ i Ä‘Äƒng.
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
