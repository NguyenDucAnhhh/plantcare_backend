package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class CareTipRequest {
    private String title;
    private String content; // Nột dung (Kh�ng bắt buộc)
    private String imageUrl; // Bắt buộc D-2
}
