package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class CommentRequest {
    private String content;
    private Long parentCommentId; // T�y chọn nếu muốn Reply b�nh luận Đệ quy
}
