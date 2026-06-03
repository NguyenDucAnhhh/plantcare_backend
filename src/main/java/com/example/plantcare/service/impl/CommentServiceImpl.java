package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.CommentRequest;
import com.example.plantcare.dto.response.CommentResponse;
import com.example.plantcare.model.Comment;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.CommentRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.plantcare.model.Notification;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.service.FirebasePushService;
import com.example.plantcare.model.NotificationType;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final FirebasePushService firebasePushService;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
    }

    @Override
    public CommentResponse addComment(Long postId, CommentRequest request, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("POST_NOT_FOUND", "Bài đăng không tồn tại!"));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new com.example.plantcare.exception.AppException("COMMENT_NOT_FOUND", "Bình luận gốc không tồn tại!"));
        }

        Comment newComment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .parentComment(parentComment)
                .build();
        
        Comment savedComment = commentRepository.save(newComment);

        // GUI THONG BAO
        Set<Long> notifiedUserIds = new HashSet<>();
        notifiedUserIds.add(author.getId()); // Khong gui thong bao cho chinh minh

        // 1. Thong bao cho chu bai dang
        User postAuthor = post.getAuthor();
        if (!notifiedUserIds.contains(postAuthor.getId())) {
            String title = "Bình luận mới trên bài đăng của bạn!";
            String body = author.getFullName() + " đã bình luận: " + request.getContent();
            
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
            
            notifiedUserIds.add(postAuthor.getId());
        }

        // 2. Thong bao cho nguoi duoc reply (Neu co)
        if (parentComment != null) {
            User parentCommentAuthor = parentComment.getAuthor();
            if (!notifiedUserIds.contains(parentCommentAuthor.getId())) {
                String title = "Có người trả lời bình luận của bạn!";
                String body = author.getFullName() + " đã trả lời: " + request.getContent();
                
                Notification notif = Notification.builder()
                        .title(title)
                        .message(body)
                        .type(NotificationType.COMMUNITY.name())
                        .recipient(parentCommentAuthor)
                        .targetId(post.getId())
                        .createdAt(LocalDateTime.now())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);
                firebasePushService.sendPushNotification(parentCommentAuthor, NotificationType.COMMUNITY, title, body);
            }
        }

        return CommentResponse.fromEntity(savedComment, email);
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId, String currentUserEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("POST_NOT_FOUND", "Bài đăng không tồn tại!"));

        return post.getComments().stream()
                .map(c -> CommentResponse.fromEntity(c, currentUserEmail))
                .collect(Collectors.toList());
    }

}
