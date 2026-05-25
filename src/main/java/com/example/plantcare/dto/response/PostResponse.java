package com.example.plantcare.dto.response;

import com.example.plantcare.model.Post;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

@Data
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private List<String> imageUrls;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private int likeCount;
    private int commentCount;
    @JsonProperty("isLiked")
    private boolean isLiked;
    @JsonProperty("isMine")
    private boolean isMine;
    private LocalDateTime createdAt;

    public static PostResponse fromEntity(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrls(post.getImageUrls())
                .authorId(post.getAuthor().getId())
                .authorName(post.getAuthor().getFullName())
                .authorAvatar(post.getAuthor().getAvatarUrl())
                .likeCount(post.getLikeCount())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
