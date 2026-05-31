package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class CareTipRequest {
    private String title;
    private String content; // Nột dung
    private String imageUrl; // Bắt buộc D-2
    private String category;
}
