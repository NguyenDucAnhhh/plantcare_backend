package com.example.plantcare.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminPostResponse {
    private Long id;
    private String authorName;
    private String authorEmail;
    private String content;
    private List<String> imageUrls;
    private boolean isVisible;
    private int likeCount;
    private LocalDateTime createdAt;
}
