package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class PostRequest {
    private String content;
    private java.util.List<String> imageUrls;
}
