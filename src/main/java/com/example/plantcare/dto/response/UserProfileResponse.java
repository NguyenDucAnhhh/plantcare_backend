package com.example.plantcare.dto.response;

import com.example.plantcare.model.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String bio;
    private int followersCount;
    private int followingCount;
    private LocalDateTime createdAt;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
