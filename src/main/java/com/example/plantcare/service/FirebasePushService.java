package com.example.plantcare.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.plantcare.model.User;
import com.example.plantcare.model.NotificationType;

@Slf4j
@Service
public class FirebasePushService {

    public void sendPushNotification(User targetUser, NotificationType type, String title, String body) {
        if (targetUser == null || targetUser.getFcmToken() == null || targetUser.getFcmToken().isBlank()) {
            log.warn("[Firebase] FCM Token rong hoac User null, bo qua viec gui thong bao.");
            return;
        }

        if (!targetUser.isNotifyAll()) return;
        if (type == NotificationType.COMMUNITY && !targetUser.isNotifyCommunity()) return;
        if (type == NotificationType.REMINDER && !targetUser.isNotifyReminder()) return;
        if (type == NotificationType.SYSTEM && !targetUser.isNotifySystem()) return;

        String fcmToken = targetUser.getFcmToken();
        log.info("[Firebase] Dang gui toi Token: {}", fcmToken);

        try {
            Message message = Message.builder()
                    .putData("title", title)
                    .putData("body", body)
                    .putData("type", type.name())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setAndroidConfig(com.google.firebase.messaging.AndroidConfig.builder()
                            .setPriority(com.google.firebase.messaging.AndroidConfig.Priority.HIGH)
                            .setNotification(com.google.firebase.messaging.AndroidNotification.builder()
                                    .setTitle(title)
                                    .setBody(body)
                                    .setChannelId("high_importance_channel")
                                    .build())
                            .build())
                    .setToken(fcmToken)
                    .build();

            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[Firebase] Push thanh cong! Message ID: {}", messageId);

        } catch (Exception e) {
            log.error("[Firebase] Loi gui Push Notification: {}", e.getMessage());
        }
    }
}