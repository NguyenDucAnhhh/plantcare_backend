package com.example.plantcare.service;
import com.example.plantcare.model.NotificationType;
import com.example.plantcare.model.Reminder;
import com.example.plantcare.repository.ReminderRepository;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderSchedulerService {
    private final ReminderRepository reminderRepository;
    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processReminders() {
        LocalTime now = LocalTime.now();
        LocalTime currentMinute = LocalTime.of(now.getHour(),
                now.getMinute());
        List<Reminder> dueReminders = reminderRepository.findByTriggerTime(currentMinute);
        for (Reminder reminder : dueReminders) {
            com.example.plantcare.dto.response.ReminderResponse responseDTO = com.example.plantcare.dto.response.ReminderResponse.fromEntity(reminder);
            if (responseDTO.getNextExecution() != null && responseDTO.getNextExecution().toLocalDate().equals(LocalDate.now())) {
                String title = "Đến giờ chăm sóc cây!";
                switch(reminder.getType().name()) {
                    case "WATERING": title = "Đã đến giờ tưới nước!"; break;
                    case "FERTILIZING": title = "Đã đến giờ bón phân!"; break;
                    case "MISTING": title = "Đã đến giờ phun sương!"; break;
                    case "ROTATING": title = "Đã đến giờ xoay cây!"; break;
                    case "PRUNING": title = "Đã đến giờ cắt tỉa!"; break;
                }
                String body = "Hãy chăm sóc cho cây " + reminder.getPlant().getName() + " của bạn nhé.";
                
                // Kiểm tra cài đặt thông báo toàn cục của User trước khi lưu in-app notification
                com.example.plantcare.model.User user = reminder.getPlant().getGarden().getUser();
                if (user.isNotifyAll() && user.isNotifyReminder()) {
                    firebasePushService.sendPushNotification(
                            user,
                            NotificationType.REMINDER,
                            title,
                            body
                    );
                    
                    // Save IN-APP Notification to Database
                    Notification notif = Notification.builder()
                            .recipient(user)
                            .title(title)
                            .message(body)
                            .type("REMINDER")
                            .targetId(reminder.getId())
                            .isRead(false)
                            .build();
                    notificationRepository.save(notif);
                }
                
                log.info("Sent reminder for plant: {}", reminder.getPlant().getName());
            }
        }
    }
}