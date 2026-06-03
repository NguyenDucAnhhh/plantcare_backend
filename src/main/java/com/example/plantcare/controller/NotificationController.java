package com.example.plantcare.controller;

import com.example.plantcare.dto.response.NotificationResponse;
import com.example.plantcare.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Thông báo", description = "Hộp thư Thông báo hệ thống")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Lấy toàn bộ thông báo của tôi", description = "Tự động sắp xếp mới nhất lên đầu")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(Authentication authentication) {
        return ResponseEntity.ok(notificationService.getMyNotifications(authentication.getName()));
    }

    @Operation(summary = "Đánh dấu 1 thông báo là ĐÃ ĐỌC")
    @PatchMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        notificationService.markAsRead(id, authentication.getName());
        return ResponseEntity.ok("Đã đọc!");
    }

    @Operation(summary = "Đánh dấu TẤT CẢ là ĐÃ ĐỌC")
    @PatchMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(Authentication authentication) {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok("Đã dọn dẹp sạch sẽ hộp thư!");
    }
}
