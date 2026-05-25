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

    // Gắn vào Bài Post nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @ToString.Exclude
    private Post post;

    // Ai là người múa phím (Tác giả cmt)?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User author;

    // HỖ TRỢ TRẢ LỜI BÌNH LUẬN (Nested Comments/Replies):
    // Bản thân nó cũng có thể chứa 1 thẻ cắm vào 1 Bình luận Gốc 
    // (Nếu nó mang giá trị Null thì đây là Bình luận nổi cấp 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @ToString.Exclude
    private Comment parentComment;

    // VÀ MỘT BÌNH LUẬN GỐC CÓ THỂ CHỨA VÔ SỐ BÌNH LUẬN TRẢ LỜI THẾ HỆ CON:
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private java.util.List<Comment> replies = new java.util.ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
