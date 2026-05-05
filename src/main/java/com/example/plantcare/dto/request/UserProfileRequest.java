package com.example.plantcare.dto.request;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String fullName;
    private String bio;
    private String avatarUrl;
}
