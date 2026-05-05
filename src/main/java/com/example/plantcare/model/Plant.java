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
    private String name; // TÃªn Ä‘á»‹nh danh do ngÆ°á»i dÃ¹ng tá»± Ä‘áº·t (CÃ¢y hoa há»“ng ngoáº¡i)

    private String species; // LoÃ i sinh há»c (Rosa)

    @Column(columnDefinition = "TEXT")
    private String description; // MÃ´ táº£ quÃ¡ trÃ¬nh lá»›n lÃªn cá»§a cÃ¢y

    private String imageUrl; // Chá»©a link URL cá»§a Amazon S3/Cloudinary

    private LocalDate datePlanted; // TÃ­ch chá»n ngÃ y gieo háº¡t / trá»“ng vÃ o cháº­u

    // Má»‘i quan há»‡ MANY_TO_ONE: Má»™t CÃ¢y báº¯t buá»™c pháº£i Ä‘Æ°á»£c Ä‘áº·t trong 1 VÆ°á»n
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "garden_id", nullable = false)
    @ToString.Exclude
    private Garden garden;

    // Äáº¶C Táº¢ "XÃ“A CÃ‚Y LÃ€ XÃ“A LUÃ”N Lá»ŠCH TRÃŒNH": 
    // RÃ ng buá»™c Cascade Delete Ä‘áº£m báº£o DB triá»‡t tiÃªu toÃ n bá»™ thÆ° má»¥c Reminder bÃªn trong khi CÃ¢y bá»‹ Tráº£m.
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private java.util.List<Reminder> reminders = new java.util.ArrayList<>();
}
