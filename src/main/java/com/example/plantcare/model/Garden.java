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

    // TÃªn khu vÆ°á»n (VD: VÆ°á»n xÆ°Æ¡ng rá»“ng)
    @Column(nullable = false)
    private String name;

    // Vá»‹ trÃ­ (VD: Ban cÃ´ng, SÃ¢n thÆ°á»£ng)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description; // Phá»¥c vá»¥ chá»©c nÄƒng ghi chÃº mÃ´ táº£ vÆ°á»n

    private String imageUrl; // áº¢nh vÆ°á»n cÃ¢y

    // Má»‘i quan há»‡ MANY_TO_ONE: Nháº­p VÆ°á»n vÃ o há»‡ thá»‘ng pháº£i biáº¿t VÆ°á»n nÃ y cá»§a Ai.
    // Nhiá»u VÆ°á»n thuá»™c vá» 1 User.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude // NgÄƒn cháº·n vÃ²ng láº·p in chá»¯ Ä‘á»‡ quy gÃ¢y sáº­p RAM
    private User user;

    // Má»‘i quan há»‡ ONE_TO_MANY: 1 VÆ°á»n chá»©a Nhiá»u CÃ¢y.
    // Lá»‡nh Tá»‘i Cao ÄÃ¡ng Sá»£ Báº­c Nháº¥t (cascade = CascadeType.ALL, orphanRemoval = true):
    // Phá»¥c vá»¥ cho chá»©c nÄƒng UC11 (XÃ³a VÆ°á»n)! Khi VÆ°á»n bá»‹ xÃ³a, Postgres sáº½ báº¯n lá»‡nh chÃ©m sáº¡ch 
    // bay máº§u toÃ n bá»™ cÃ¢y trá»“ng (Plant) bÃªn trong cÃ¡i vÆ°á»n Ä‘Ã³ khá»i CSDL.
    @OneToMany(mappedBy = "garden", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Plant> plants = new ArrayList<>();
}
