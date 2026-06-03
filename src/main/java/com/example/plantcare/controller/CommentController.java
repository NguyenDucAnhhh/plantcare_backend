package com.example.plantcare.controller;

import com.example.plantcare.dto.request.CommentRequest;
import com.example.plantcare.dto.response.CommentResponse;
import com.example.plantcare.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Tag(name = "Bình Luận", description = "Đánh giá, thảo luận trên bài đăng")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Thêm bình luận mới (Có thể Reply)")
    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(postId, request, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ bình luận của 1 bài đăng")
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, email));
    }


}
