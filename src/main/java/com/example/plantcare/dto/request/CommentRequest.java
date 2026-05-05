package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class CommentRequest {
    private String content;
    private Long parentCommentId; // TÃƒÂ¹y chÃ¡Â»Ân nÃ¡ÂºÂ¿u muÃ¡Â»â€˜n Reply bÃƒÂ¬nh luÃ¡ÂºÂ­n Ã„ÂÃ¡Â»â€¡ quy
}
