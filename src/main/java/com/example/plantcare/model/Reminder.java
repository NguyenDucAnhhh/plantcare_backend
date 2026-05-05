package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Loáº¡i bÃ¡o thá»©c: TÆ°á»›i nÆ°á»›c, bÃ³n phÃ¢n
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderType type;

    // Giá» sáº½ rung chuÃ´ng bÃ¡o thá»©c (Giá»‘ng há»‡t app Äá»“ng Há»“ bÃ¡o thá»©c trÃªn IOS/Android)
    // LÆ°u Ã½: LocalTime chá»‰ lÆ°u Giá»:PhÃºt, khÃ´ng dÃ­nh Ä‘áº¿n NgÃ y/ThÃ¡ng.
    // VÃ­ dá»¥: 07:30
    @Column(nullable = false)
    private LocalTime triggerTime;

    // Quy táº¯c Ä‘iá»ƒm danh láº·p láº¡i tuáº§n hoÃ n: VD "MON,WED,FRI", "EVERYDAY", hoáº·c "NONE"
    @Column(nullable = false)
    private String repeatDays;

    // CÃ¡i cÃ´ng táº¯c NÃºt gáº¡t mÃ u xanh (Toggle) trong App. 
    // Táº¯t cÃ´ng táº¯c thÃ¬ Server tá»± hiá»ƒu khÃ´ng gá»­i thÃ´ng bÃ¡o Push ná»¯a.
    @Column(nullable = false)
    private boolean isActive = true;

    // Lá»‹ch Háº¹n nÃ y dÃ nh cho cÃ¡i CÃ¢y cá»¥ thá»ƒ nÃ o?
    // MANY_TO_ONE: 1 CÃ¡i cÃ¢y cÃ³ thá»ƒ cÃ i táº­n 3-4 loáº¡i BÃ¡o thá»©c khÃ¡c nhau (1 bÃ¡o thá»©c tÆ°á»›i, 1 bÃ¡o thá»©c tá»‰a cÃ nh...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    @ToString.Exclude
    private Plant plant;
}
