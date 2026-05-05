package com.example.plantcare.service;

import com.example.plantcare.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(String email);
    void markAsRead(Long notificationId, String email);
    void markAllAsRead(String email);
}
