package com.example.plantcare.controller;

import com.example.plantcare.dto.request.PostRequest;
import com.example.plantcare.dto.response.PostResponse;
import com.example.plantcare.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "5. Không Gian Cộng Đồng", description = "Quản lý Bài viết (Mạng xã hội)")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Đăng bài viết mới")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(request, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ bài viết trên Newfeed")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllVisiblePosts() {
        return ResponseEntity.ok(postService.getAllVisiblePosts());
    }

    @Operation(summary = "Lấy danh sách bài viết của tôi")
    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getMyPosts(authentication.getName()));
    }

    @Operation(summary = "Xem chi tiết 1 bài viết")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @Operation(summary = "Sửa bài viết của chính mình")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(postId, request, authentication.getName()));
    }

    @Operation(summary = "Xóa bài viết")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {
        postService.deletePost(postId, authentication.getName());
        return ResponseEntity.ok("Xóa bài viết thành công!");
    }
}
