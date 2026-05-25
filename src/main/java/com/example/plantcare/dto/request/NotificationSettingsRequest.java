package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class NotificationSettingsRequest {
    private boolean notifyAll;
    private boolean notifyCommunity;
    private boolean notifyReminder;
    private boolean notifySystem;
    private String fcmToken;
}
