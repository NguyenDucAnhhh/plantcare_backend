package com.example.plantcare.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // Gáº¯n vÃ o BÃ i Post nÃ o?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    // Ai lÃ  ngÆ°á»i mÃºa phÃ­m (TÃ¡c giáº£ cmt)?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User author;

    // Há»– TRá»¢ TRáº¢ Lá»œI BÃŒNH LUáº¬N (Nested Comments/Replies):
    // Báº£n thÃ¢n nÃ³ cÅ©ng cÃ³ thá»ƒ chá»©a 1 tháº» cáº¯m vÃ o 1 BÃ¬nh luáº­n Gá»‘c 
    // (Náº¿u nÃ³ mang giÃ¡ trá»‹ Null thÃ¬ Ä‘Ã¢y lÃ  BÃ¬nh luáº­n ná»•i cáº¥p 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @ToString.Exclude
    private Comment parentComment;

    // VÃ€ Má»˜T BÃŒNH LUáº¬N Gá»C CÃ“ THá»‚ CHá»¨A VÃ” Sá» BÃŒNH LUáº¬N TRáº¢ Lá»œI THáº¾ Há»† CON:
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private java.util.List<Comment> replies = new java.util.ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
