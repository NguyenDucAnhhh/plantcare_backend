package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class CommentRequest {
    private String content;
    private Long parentCommentId; // Tuy chọn nếu muốn Reply binh luận Đệ quy
}
