package com.example.plantcare.service.impl;

import com.example.plantcare.dto.response.UserProfileResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.CloudinaryService;
import com.example.plantcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {
        User user = getUserByEmail(email);
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
        return response;
    }

    @Override
    public UserProfileResponse getUserProfileById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));
        UserProfileResponse response = UserProfileResponse.fromEntity(user);
        response.setFollowersCount(userRepository.countFollowers(user.getId()));
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
            // Upload len Cloudinary thu muc 'avatars'
            String avatarUrl = cloudinaryService.uploadImage(file, "avatars");
            user.setAvatarUrl(avatarUrl);
            user = userRepository.save(user);
            
            UserProfileResponse response = UserProfileResponse.fromEntity(user);
            response.setFollowersCount(userRepository.countFollowers(user.getId()));
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload ảnh đại diện: " + e.getMessage());
        }
    }

    @Override
    public boolean toggleFollow(Long targetUserId, String email) {
        User currentUser = getUserByEmail(email);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại!"));

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new RuntimeException("Bạn không thể tự theo dõi chính mình!");
        }

        boolean isAlreadyFollowing = currentUser.getFollowing().contains(targetUser);

        if (isAlreadyFollowing) {
            currentUser.getFollowing().remove(targetUser); // Hủy Follow
        } else {
            currentUser.getFollowing().add(targetUser); // Bấm Follow
        }

        userRepository.save(currentUser);
        return !isAlreadyFollowing; // Trả về trạng thái hiện tại (true = đang follow)
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
}
