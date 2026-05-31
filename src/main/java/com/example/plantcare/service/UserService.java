package com.example.plantcare.service;

import com.example.plantcare.dto.response.UserProfileResponse;
import com.example.plantcare.dto.request.NotificationSettingsRequest;
import com.example.plantcare.dto.request.ChangePasswordRequest;

import java.util.List;

public interface UserService {
    UserProfileResponse getMyProfile(String email);
    void updateFcmToken(String email, String fcmToken);
    UserProfileResponse getUserProfileById(Long userId, String currentUserEmail);

    UserProfileResponse updateNotificationSettings(String email, NotificationSettingsRequest request);
    void changePassword(String email, ChangePasswordRequest request);
    UserProfileResponse updateProfile(com.example.plantcare.dto.request.UserProfileRequest request, String email);
    UserProfileResponse uploadAvatar(org.springframework.web.multipart.MultipartFile file, String email);
    boolean toggleFollow(Long targetUserId, String email);
    List<UserProfileResponse> getMyFollowings(String email);
    List<UserProfileResponse> searchUsers(String keyword, String currentUserEmail);
}
