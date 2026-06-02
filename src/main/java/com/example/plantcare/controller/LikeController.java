package com.example.plantcare.controller;

import com.example.plantcare.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
@Tag(name = "7. Tương Tác Cảm Xúc", description = "Tính năng Thả Tim")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Thả tim / Hủy thả tim", description = "Bấm lần 1 là Thả tim, bấm lần 2 là Hủy tim")
    @PostMapping
    public ResponseEntity<String> toggleLike(
            @PathVariable Long postId,
            Authentication authentication) {
        boolean isLiked = likeService.toggleLike(postId, authentication.getName());
        if (isLiked) {
            return ResponseEntity.ok("Đã THẢ TIM bài đăng!");
        } else {
            return ResponseEntity.ok("Đã HỦY TIM bài đăng!");
        }
    }
}
