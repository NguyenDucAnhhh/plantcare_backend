package com.example.plantcare.controller;

import com.example.plantcare.dto.response.UserProfileResponse;
import com.example.plantcare.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "8. Cư Dân Làng Cây", description = "Quản lý Hồ sơ cá nhân và Bạn bè")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Xem hồ sơ của bản thân")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyProfile(authentication.getName()));
    }

    @Operation(summary = "Xem hồ sơ của người khác")
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserProfileById(userId));
    }

    @Operation(summary = "Cập nhật hồ sơ cá nhân")
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody com.example.plantcare.dto.request.UserProfileRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateProfile(request, authentication.getName()));
    }

    @Operation(summary = "Tải lên ảnh đại diện")
    @PostMapping(value = "/me/avatar/upload", consumes = "multipart/form-data")
    public ResponseEntity<UserProfileResponse> uploadAvatar(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication) {
        return ResponseEntity.ok(userService.uploadAvatar(file, authentication.getName()));
    }

    @Operation(summary = "Theo dõi / Hủy theo dõi một người", description = "Cơ chế Bật/Tắt (Toggle)")
    @PostMapping("/{userId}/follow")
    public ResponseEntity<String> toggleFollow(
            @PathVariable Long userId,
            Authentication authentication) {
        boolean isFollowing = userService.toggleFollow(userId, authentication.getName());
        if (isFollowing) {
            return ResponseEntity.ok("Đã THEO DÕI người dùng này!");
        } else {
            return ResponseEntity.ok("Đã HỦY THEO DÕI!");
        }
    }

    @Operation(summary = "Lấy danh sách Idol (Những người mình đang Follow)")
    @GetMapping("/me/following")
    public ResponseEntity<List<UserProfileResponse>> getMyFollowings(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyFollowings(authentication.getName()));
    }
}
