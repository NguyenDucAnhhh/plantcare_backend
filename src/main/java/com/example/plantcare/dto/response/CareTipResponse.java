package com.example.plantcare.dto.response;

import com.example.plantcare.model.CareTip;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CareTipResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    public static CareTipResponse fromEntity(CareTip careTip) {
        return CareTipResponse.builder()
                .id(careTip.getId())
                .title(careTip.getTitle())
                .content(careTip.getContent())
                .imageUrl(careTip.getImageUrl())
                .createdAt(careTip.getCreatedAt())
                .build();
    }
}
