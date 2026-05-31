package com.example.plantcare.service.impl;

import com.example.plantcare.dto.response.NotificationResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Notification;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String email) {
        User user = getUserByEmail(email);
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .map(NotificationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId, String email) {
        User user = getUserByEmail(email);
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Thông báo không tồn tại!"));

        if (!notif.getRecipient().getId().equals(user.getId())) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_NOTIFICATION_ACCESS", "Bạn không có quyền thao tác thông báo này!");
        }

        notif.setRead(true);
        notificationRepository.save(notif);
    }

    @Override
    public void markAllAsRead(String email) {
        User user = getUserByEmail(email);
        List<Notification> unreadNotifs = notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .filter(n -> !n.isRead())
                .collect(Collectors.toList());

        unreadNotifs.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unreadNotifs);
    }
}

