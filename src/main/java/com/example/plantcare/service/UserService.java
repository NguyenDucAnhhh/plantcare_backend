package com.example.plantcare.service;

import com.example.plantcare.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse getMyProfile(String email);
    UserProfileResponse getUserProfileById(Long userId);
    UserProfileResponse updateProfile(com.example.plantcare.dto.request.UserProfileRequest request, String email);
    UserProfileResponse uploadAvatar(org.springframework.web.multipart.MultipartFile file, String email);
    boolean toggleFollow(Long targetUserId, String email);
    List<UserProfileResponse> getMyFollowings(String email);
}
