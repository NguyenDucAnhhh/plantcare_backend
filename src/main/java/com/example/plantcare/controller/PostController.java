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
import org.springframework.web.multipart.MultipartFile;
import com.example.plantcare.service.CloudinaryService;
import java.util.ArrayList;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "5. Không Gian Cộng Đồng", description = "Quản lý Bài viết (Mạng xã hội)")
public class PostController {

    private final PostService postService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload ảnh cho bài viết", description = "Upload nhiều ảnh lên Cloudinary và trả về danh sách link ảnh")
    @PostMapping(value = "/images/upload", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadPostImages(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = cloudinaryService.uploadImage(file, "posts");
                imageUrls.add(imageUrl);
            }
            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi upload ảnh bài viết: " + e.getMessage());
        }
    }

    @Operation(summary = "Đăng bài viết mới")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(request, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ bài viết trên Newfeed")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllVisiblePosts(Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getAllVisiblePosts(email));
    }

    @Operation(summary = "Lấy danh sách bài viết đang theo dõi")
    @GetMapping("/following")
    public ResponseEntity<List<PostResponse>> getFollowingPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getFollowingPosts(authentication.getName()));
    }

    @Operation(summary = "Lấy danh sách bài viết của tôi")
    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getMyPosts(authentication.getName()));
    }

    @Operation(summary = "Xem chi tiết 1 bài viết")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getPostById(postId, email));
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
