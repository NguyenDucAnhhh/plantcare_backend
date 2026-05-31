package com.example.plantcare.dto.request;

import lombok.Data;

@Data
public class NotificationSettingsRequest {
    @com.fasterxml.jackson.annotation.JsonProperty("notifyAll") private boolean notifyAll;
    @com.fasterxml.jackson.annotation.JsonProperty("notifyCommunity") private boolean notifyCommunity;
    @com.fasterxml.jackson.annotation.JsonProperty("notifyReminder") private boolean notifyReminder;
    @com.fasterxml.jackson.annotation.JsonProperty("notifySystem") private boolean notifySystem;
    private String fcmToken;
}
