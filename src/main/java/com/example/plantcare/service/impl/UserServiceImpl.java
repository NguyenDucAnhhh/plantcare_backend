package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.ChangePasswordRequest;
import com.example.plantcare.dto.request.NotificationSettingsRequest;
import com.example.plantcare.dto.response.UserProfileResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.NotificationType;
import com.example.plantcare.model.User;
import com.example.plantcare.model.Notification;
import com.example.plantcare.repository.NotificationRepository;
import java.time.LocalDateTime;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.CloudinaryService;
import com.example.plantcare.service.FirebasePushService;
import com.example.plantcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final FirebasePushService firebasePushService;
    private final NotificationRepository notificationRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public void updateFcmToken(String email, String fcmToken) {
        User user = getUserByEmail(email);
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        return response;
    }

    @Override
    @Transactional
    public UserProfileResponse getUserProfileById(Long userId, String currentUserEmail) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        UserProfileResponse response = UserProfileResponse.fromEntity(targetUser);
        response.setFollowersCount(userRepository.countFollowers(targetUser.getId()));
        
        if (currentUserEmail != null) {
            User currentUser = getUserByEmail(currentUserEmail);
            response.setFollowing(currentUser.getFollowing().contains(targetUser));
        } else {
            response.setFollowing(false);
        }
        
        return response;
    }

    @Override
    public UserProfileResponse updateProfile(com.example.plantcare.dto.request.UserProfileRequest request, String email) {
        User user = getUserByEmail(email);
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        
        user = userRepository.save(user);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        return response;
    }

    @Override
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
            throw new com.example.plantcare.exception.AppException("UPLOAD_AVATAR_FAILED", "Lỗi khi upload ảnh đại diện: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean toggleFollow(Long targetUserId, String email) {
        User currentUser = getUserByEmail(email);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new com.example.plantcare.exception.AppException("USER_CANNOT_FOLLOW_SELF", "Bạn không thể tự theo dõi chính mình!");
        }

        boolean isAlreadyFollowing = currentUser.getFollowing().contains(targetUser);

        if (isAlreadyFollowing) {
            currentUser.getFollowing().remove(targetUser); // H?y Follow
        } else {
            currentUser.getFollowing().add(targetUser); // B?m Follow
            String title = "Có người theo dõi mới";
            String body = currentUser.getFullName() + " đã bắt đầu theo dõi bạn.";
            
            Notification notif = Notification.builder()
                    .title(title)
                    .message(body)
                    .type(NotificationType.COMMUNITY.name())
                    .recipient(targetUser)
                    .targetId(currentUser.getId())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .build();
            notificationRepository.save(notif);
            
            firebasePushService.sendPushNotification(targetUser, NotificationType.COMMUNITY, title, body);
        }

        userRepository.save(currentUser);
        return !isAlreadyFollowing; // Trả về trạng thái hiện tại (true = đang follow)
    }

    @Override
    @Transactional
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
    @Transactional
    public UserProfileResponse updateNotificationSettings(String email, NotificationSettingsRequest request) {
        User user = getUserByEmail(email);

        user.setNotifyAll(request.isNotifyAll());
        user.setNotifyCommunity(request.isNotifyCommunity());
        user.setNotifyReminder(request.isNotifyReminder());
        user.setNotifySystem(request.isNotifySystem());
        
        if (request.getFcmToken() != null && !request.getFcmToken().trim().isEmpty()) {
            user.setFcmToken(request.getFcmToken());
        }

        user = userRepository.save(user);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        return response;
    }

    @Override
    @Transactional
    public List<UserProfileResponse> searchUsers(String keyword, String currentUserEmail) {
        return userRepository.searchUsers(keyword).stream()
            .map(user -> {
                UserProfileResponse res = UserProfileResponse.fromEntity(user);
                res.setFollowersCount(userRepository.countFollowers(user.getId()));
                if (currentUserEmail != null) {
                    try {
                        User currentUser = getUserByEmail(currentUserEmail);
                        res.setFollowing(currentUser.getFollowing().contains(user));
                    } catch (Exception e) {
                        res.setFollowing(false);
                    }
                } else {
                    res.setFollowing(false);
                }
                return res;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getUserByEmail(email);
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new com.example.plantcare.exception.AppException("USER_WRONG_OLD_PASSWORD", "Mật khẩu cũ không chính xác!");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}

