package com.example.plantcare.dto.response;

import com.example.plantcare.model.Comment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    @JsonProperty("isMine")
    private boolean isMine;

    public static CommentResponse fromEntity(Comment comment, String currentUserEmail) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getFullName())
                .authorAvatar(comment.getAuthor().getAvatarUrl())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .isMine(currentUserEmail != null && comment.getAuthor().getEmail().equals(currentUserEmail))
                .build();
    }
}
