package com.example.plantcare.dto.response;

import com.example.plantcare.model.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private String authorName;
    private int likeCount;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostResponse fromEntity(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .authorName(post.getAuthor().getFullName())
                .likeCount(post.getLikeCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
