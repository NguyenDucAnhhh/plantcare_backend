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
@Tag(name = "6. Khu Vực Bình Luận", description = "Đánh giá, thảo luận trên bài viết")
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

    @Operation(summary = "Lấy toàn bộ bình luận của 1 bài viết")
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }

    @Operation(summary = "Xóa bình luận")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long postId, // Chữ postId ở đây chỉ để đúng URL pattern
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok("Xóa bình luận thành công!");
    }
}
