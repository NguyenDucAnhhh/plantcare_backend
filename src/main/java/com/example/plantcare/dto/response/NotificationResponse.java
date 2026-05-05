package com.example.plantcare.dto.response;

import com.example.plantcare.model.Notification;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String type;
    private Long targetId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notif) {
        return NotificationResponse.builder()
                .id(notif.getId())
                .title(notif.getTitle())
                .message(notif.getMessage())
                .type(notif.getType())
                .targetId(notif.getTargetId())
                .isRead(notif.isRead())
                .createdAt(notif.getCreatedAt())
                .build();
    }
}
