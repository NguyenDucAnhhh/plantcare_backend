package com.example.plantcare.service.impl;

import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.LikeAction;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.model.Notification;
import com.example.plantcare.repository.LikeActionRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.plantcare.service.FirebasePushService;
import com.example.plantcare.model.NotificationType;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeActionRepository likeActionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;

    @Override
    public boolean toggleLike(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("POST_NOT_FOUND", "Bài viết không tồn tại!"));

        // Tìm xem user này đã thả tim bài này chưa (Yêu cầu repo có hàm findByUserAndPost)
        Optional<LikeAction> existingLike = likeActionRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            likeActionRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false;
        } else {
            LikeAction newLike = LikeAction.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeActionRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            User postAuthor = post.getAuthor();
            if (!postAuthor.getId().equals(user.getId())) {
                String title = "Có người thích bài viết của bạn!";
                String body = user.getFullName() + " vừa thả tim bài viết của bạn.";
                
                Notification notif = Notification.builder()
                        .title(title)
                        .message(body)
                        .type(NotificationType.COMMUNITY.name())
                        .recipient(postAuthor)
                        .targetId(post.getId())
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
                
                firebasePushService.sendPushNotification(postAuthor, NotificationType.COMMUNITY, title, body);
            }

            return true;
        }
    }
}
