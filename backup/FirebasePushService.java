package com.example.plantcare.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.plantcare.model.User;

/**
 * TANG SERVICE - FIREBASE PUSH NOTIFICATION
 * ==========================================
 * - FirebaseMessaging.getInstance() lay doi tuong tu FirebaseApp da khoi tao
 *   trong FirebaseConfig (KHONG can Inject gi them, Firebase tu quan ly Singleton cua no).
 * - Ham sendPushNotification(): Gui thong bao Push toi 1 thiet bi dua vao FCM Token.
 *
 * FCM Token la gi?
 *   - Moi lan Flutter App khoi dong, Firebase cap cho dien thoai do 1 cai Token duy nhat.
 *   - Flutter gui Token nay len Spring Boot de luu vao cot fcmToken trong bang users.
 *   - Moi khi can bam chuong bao, Spring Boot lay Token tu DB va ban qua Firebase Server.
 *
 * HOW IT WORKS (Luong chay thuc te):
 *   User A binh luan vao bai cua User B
 *   -> CommentService luu binh luan xong
 *   -> Goi FirebasePushService.sendPushNotification(fcmToken cua B, "Tin nhan")
 *   -> Firebase Server nhan lenh, ban Push xuong dien thoai cua B
 *   -> Dien thoai B rung chuong thong bao
 */
@Slf4j
@Service
public class FirebasePushService {

    /**
     * Bam chuong Push Notification toi 1 thiet bi.
     * @param fcmToken  Token FCM cua thiet bi nhan (lay tu DB)
     * @param title     Tieu de thong bao (Vi du: "Nguoi dung vua like bai cua ban!")
     * @param body      Noi dung chi tiet thong bao
     */
    public void sendPushNotification(com.example.plantcare.model.User user, String title, String body, String type) {
        if (!user.isNotifyAll()) return;

        if ("REMINDER".equals(type) && !user.isNotifyReminder()) return;
        if (("LIKE".equals(type) || "COMMENT".equals(type) || "FOLLOWER".equals(type)) && !user.isNotifyCommunity()) return;

        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isBlank()) {
            log.warn("[Firebase] FCM Token rong, bo qua viec gui thong bao.");
            return;
        }

        try {
            // Xay dung goi tin nhan
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(fcmToken) // Chi dinh thiet bi nhan
                    .build();

            // Ban qua Firebase Server (Bat dong bo)
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[Firebase] Push thanh cong! Message ID: {}", messageId);

        } catch (Exception e) {
            log.error("[Firebase] Loi gui Push Notification: {}", e.getMessage());
        }
    }
}
