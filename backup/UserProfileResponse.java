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
    @com.fasterxml.jackson.annotation.JsonProperty("isFollowing")
    private boolean isFollowing;
    private LocalDateTime createdAt;
    @com.fasterxml.jackson.annotation.JsonProperty("notifyAll") private boolean notifyAll;
    @com.fasterxml.jackson.annotation.JsonProperty("notifyCommunity") private boolean notifyCommunity;
    @com.fasterxml.jackson.annotation.JsonProperty("notifyReminder") private boolean notifyReminder;
    @com.fasterxml.jackson.annotation.JsonProperty("notifySystem") private boolean notifySystem;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .createdAt(user.getCreatedAt())
                .notifyAll(user.isNotifyAll())
                .notifyCommunity(user.isNotifyCommunity())
                .notifyReminder(user.isNotifyReminder())
                .notifySystem(user.isNotifySystem())
                .build();
    }
}
