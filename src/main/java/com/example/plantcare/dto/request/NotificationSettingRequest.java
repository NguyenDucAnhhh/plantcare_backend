package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class NotificationSettingRequest {
    private Boolean notifyAll;
    private Boolean notifyCommunity;
    private Boolean notifyReminder;
    private Boolean notifySystem;
}
