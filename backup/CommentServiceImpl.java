package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.CommentRequest;
import com.example.plantcare.dto.response.CommentResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Comment;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.model.Notification;
import com.example.plantcare.repository.CommentRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.service.CommentService;
import com.example.plantcare.service.FirebasePushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("TÃƒÆ’Ã‚Â i khoÃƒÂ¡Ã‚ÂºÃ‚Â£n khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
    }

    @Override
    @Transactional
    public CommentResponse addComment(Long postId, CommentRequest request, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("BÃƒÆ’Ã‚Â i viÃƒÂ¡Ã‚ÂºÃ‚Â¿t khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("BÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n gÃƒÂ¡Ã‚Â»Ã¢â‚¬Ëœc khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
        }

        Comment newComment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .parentComment(parentComment)
                .build();

        newComment = commentRepository.save(newComment);

        Set<Long> notifiedUserIds = new HashSet<>();

        if (parentComment != null) {
            User commentAuthor = parentComment.getAuthor();
            if (!commentAuthor.getId().equals(author.getId())) {
                String title = "PhÃƒÂ¡Ã‚ÂºÃ‚Â£n hÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“i bÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n";
                String message = author.getFullName() + " Ãƒâ€žÃ¢â‚¬ËœÃƒÆ’Ã‚Â£ trÃƒÂ¡Ã‚ÂºÃ‚Â£ lÃƒÂ¡Ã‚Â»Ã‚Âi bÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n cÃƒÂ¡Ã‚Â»Ã‚Â§a bÃƒÂ¡Ã‚ÂºÃ‚Â¡n.";
                
                Notification notif = Notification.builder()
                        .recipient(commentAuthor)
                        .title(title)
                        .message(message)
                        .type("POST")
                        .targetId(post.getId())
                        .isRead(false)
                        .build();
                notificationRepository.save(notif);

                String fcmToken = commentAuthor.getFcmToken();
                if (fcmToken != null && !fcmToken.isBlank()) {
                    firebasePushService.sendPushNotification(post.getAuthor(), title, message, "COMMENT");
                }
                notifiedUserIds.add(commentAuthor.getId());
            }
        }

        User postAuthor = post.getAuthor();
        if (!postAuthor.getId().equals(author.getId()) && !notifiedUserIds.contains(postAuthor.getId())) {
            String title = "BÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n mÃƒÂ¡Ã‚Â»Ã¢â‚¬Âºi";
            String message = author.getFullName() + " Ãƒâ€žÃ¢â‚¬ËœÃƒÆ’Ã‚Â£ bÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n vÃƒÂ¡Ã‚Â»Ã‚Â bÃƒÆ’Ã‚Â i viÃƒÂ¡Ã‚ÂºÃ‚Â¿t cÃƒÂ¡Ã‚Â»Ã‚Â§a bÃƒÂ¡Ã‚ÂºÃ‚Â¡n.";
            
            Notification notif = Notification.builder()
                    .recipient(postAuthor)
                    .title(title)
                    .message(message)
                    .type("POST")
                    .targetId(post.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notif);

            String fcmToken = postAuthor.getFcmToken();
            if (fcmToken != null && !fcmToken.isBlank()) {
                firebasePushService.sendPushNotification(post.getAuthor(), title, message, "COMMENT");
            }
        }

        return CommentResponse.fromEntity(newComment, author);
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("BÃƒÆ’Ã‚Â i viÃƒÂ¡Ã‚ÂºÃ‚Â¿t khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
                
        User currentUser = email != null ? userRepository.findByEmail(email).orElse(null) : null;

        return post.getComments().stream()
                .map(comment -> CommentResponse.fromEntity(comment, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String email) {
        User author = getUserByEmail(email);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("BÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));

        // ChÃƒÂ¡Ã‚Â»Ã¢â‚¬Â° tÃƒÆ’Ã‚Â¡c giÃƒÂ¡Ã‚ÂºÃ‚Â£ bÃƒÆ’Ã‚Â i viÃƒÂ¡Ã‚ÂºÃ‚Â¿t hoÃƒÂ¡Ã‚ÂºÃ‚Â·c tÃƒÆ’Ã‚Â¡c giÃƒÂ¡Ã‚ÂºÃ‚Â£ bÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n mÃƒÂ¡Ã‚Â»Ã¢â‚¬Âºi Ãƒâ€žÃ¢â‚¬ËœÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Â£c xÃƒÆ’Ã‚Â³a
        if (!comment.getAuthor().getId().equals(author.getId()) && !comment.getPost().getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("BÃƒÂ¡Ã‚ÂºÃ‚Â¡n khÃƒÆ’Ã‚Â´ng cÃƒÆ’Ã‚Â³ quyÃƒÂ¡Ã‚Â»Ã‚Ân xÃƒÆ’Ã‚Â³a bÃƒÆ’Ã‚Â¬nh luÃƒÂ¡Ã‚ÂºÃ‚Â­n nÃƒÆ’Ã‚Â y!");
        }

        commentRepository.delete(comment);
    }
}