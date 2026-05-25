package com.example.plantcare.service.impl;

import com.example.plantcare.dto.response.UserProfileResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.User;
import com.example.plantcare.model.Notification;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.repository.NotificationRepository;
import com.example.plantcare.service.CloudinaryService;
import com.example.plantcare.service.UserService;
import com.example.plantcare.service.FirebasePushService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("NgÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Âi dÃƒÆ’Ã‚Â¹ng khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NgÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Âi dÃƒÆ’Ã‚Â¹ng khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        
        if (email != null && !email.isBlank() && !email.equals("anonymousUser")) {
            User currentUser = getUserByEmail(email);
            boolean isFollowing = currentUser.getFollowing().contains(user);
            response.setFollowing(isFollowing);
        }
        
        return response;
    }

    @Override
    public UserProfileResponse getUserProfileById(Long userId, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NgÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Âi dÃƒÆ’Ã‚Â¹ng khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        
        if (email != null && !email.isBlank() && !email.equals("anonymousUser")) {
            User currentUser = getUserByEmail(email);
            boolean isFollowing = currentUser.getFollowing().contains(user);
            response.setFollowing(isFollowing);
        }
        
        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(com.example.plantcare.dto.request.UserProfileRequest request, String email) {
        User user = getUserByEmail(email);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());
        
        user = userRepository.save(user);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        
        if (email != null && !email.isBlank() && !email.equals("anonymousUser")) {
            User currentUser = getUserByEmail(email);
            boolean isFollowing = currentUser.getFollowing().contains(user);
            response.setFollowing(isFollowing);
        }
        
        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse uploadAvatar(MultipartFile file, String email) {
        try {
            User user = getUserByEmail(email);
            String avatarUrl = cloudinaryService.uploadImage(file, "avatars");
            user.setAvatarUrl(avatarUrl);
            user = userRepository.save(user);
            
            UserProfileResponse response = UserProfileResponse.fromEntity(user);
            response.setFollowersCount(userRepository.countFollowers(user.getId()));
            return response;
        } catch (IOException e) {
            throw new RuntimeException("LÃƒÂ¡Ã‚Â»Ã¢â‚¬â€i khi upload ÃƒÂ¡Ã‚ÂºÃ‚Â£nh Ãƒâ€žÃ¢â‚¬ËœÃƒÂ¡Ã‚ÂºÃ‚Â¡i diÃƒÂ¡Ã‚Â»Ã¢â‚¬Â¡n: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean toggleFollow(Long targetUserId, String email) {
        User currentUser = getUserByEmail(email);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NgÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Âi dÃƒÆ’Ã‚Â¹ng khÃƒÆ’Ã‚Â´ng tÃƒÂ¡Ã‚Â»Ã¢â‚¬Å“n tÃƒÂ¡Ã‚ÂºÃ‚Â¡i!"));

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("BÃƒÂ¡Ã‚ÂºÃ‚Â¡n khÃƒÆ’Ã‚Â´ng thÃƒÂ¡Ã‚Â»Ã†â€™ tÃƒÂ¡Ã‚Â»Ã‚Â± theo dÃƒÆ’Ã‚Âµi chÃƒÆ’Ã‚Â­nh mÃƒÆ’Ã‚Â¬nh!");
        }

        boolean isAlreadyFollowing = currentUser.getFollowing().contains(targetUser);

        if (isAlreadyFollowing) {
            currentUser.getFollowing().remove(targetUser); // HÃƒÂ¡Ã‚Â»Ã‚Â§y Follow
            userRepository.save(currentUser);
            return false;
        } else {
            currentUser.getFollowing().add(targetUser); // BÃƒÂ¡Ã‚ÂºÃ‚Â¥m Follow
            userRepository.save(currentUser);

            String title = "NgÃƒâ€ Ã‚Â°ÃƒÂ¡Ã‚Â»Ã‚Âi theo dÃƒÆ’Ã‚Âµi mÃƒÂ¡Ã‚Â»Ã¢â‚¬Âºi";
            String message = currentUser.getFullName() + " Ãƒâ€žÃ¢â‚¬ËœÃƒÆ’Ã‚Â£ bÃƒÂ¡Ã‚ÂºÃ‚Â¯t Ãƒâ€žÃ¢â‚¬ËœÃƒÂ¡Ã‚ÂºÃ‚Â§u theo dÃƒÆ’Ã‚Âµi bÃƒÂ¡Ã‚ÂºÃ‚Â¡n.";
            
            Notification notif = Notification.builder()
                    .recipient(targetUser)
                    .title(title)
                    .message(message)
                    .type("FOLLOWER")
                    .targetId(currentUser.getId())
                    .isRead(false)
                    .build();
            notificationRepository.save(notif);

            String fcmToken = targetUser.getFcmToken();
            if (fcmToken != null && !fcmToken.isBlank()) {
                firebasePushService.sendPushNotification(targetUser, title, message, "FOLLOWER");
            }

            return true;
        }
    }

    @Override
    public List<UserProfileResponse> getMyFollowings(String email) {
        User currentUser = getUserByEmail(email);
        return currentUser.getFollowing().stream()
                .map(user -> {
                    UserProfileResponse res = UserProfileResponse.fromEntity(user);
                    res.setFollowersCount(userRepository.countFollowers(user.getId()));
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateFcmToken(String email, String fcmToken) {
        User user = getUserByEmail(email);
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateNotificationSettings(com.example.plantcare.dto.request.NotificationSettingsRequest request, String email) {
        User user = getUserByEmail(email);
        user.setNotifyAll(request.isNotifyAll());
        user.setNotifyCommunity(request.isNotifyCommunity());
        user.setNotifyReminder(request.isNotifyReminder());
        user.setNotifySystem(request.isNotifySystem());
        userRepository.save(user);
    }
}