package com.example.plantcare.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportResponse {
    private Long id;
    private String reporterName;
    private String reporterEmail;
    
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    
    private Long postId;
    private String postContent;
    private Long postAuthorId;
    private String postAuthorName;
    private String postAuthorAvatar;
    private List<String> postImageUrls;
    private int postLikeCount;
    private int postCommentCount;
    private LocalDateTime postCreatedAt;
}