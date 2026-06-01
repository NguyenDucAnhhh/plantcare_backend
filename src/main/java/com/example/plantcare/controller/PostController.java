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
@Tag(name = "5. Khong Gian Cong Dong", description = "Quan ly Bai viet (Mang xa hoi)")
public class PostController {

    private final PostService postService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload anh cho bai viet", description = "Upload nhieu anh len Cloudinary va tra ve danh sach link anh")
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
            throw new RuntimeException("Loi upload anh bai viet: !" + e.getMessage());
        }
    }

    @Operation(summary = "Dang bai viet moi")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(request, authentication.getName()));
    }

    @Operation(summary = "Lay toan bo bai viet tren Newfeed")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllVisiblePosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getAllVisiblePosts(page, size, email));
    }

    @Operation(summary = "Lay danh sach bai viet dang theo doi")
    @GetMapping("/following")
    public ResponseEntity<List<PostResponse>> getFollowingPosts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Authentication authentication) {
        return ResponseEntity.ok(postService.getFollowingPosts(page, size, authentication.getName()));
    }

    @Operation(summary = "Lay danh sach bai viet cua toi")
    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        return ResponseEntity.ok(postService.getMyPosts(authentication.getName()));
    }

    @Operation(summary = "Lay danh sach bai viet cua mot nguoi dung khac")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts(@PathVariable Long userId, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getUserPosts(userId, email));
    }

    @Operation(summary = "Xem chi tiet 1 bai viet")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.getPostById(postId, email));
    }

    @Operation(summary = "Sua bai viet cua chinh minh")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(postId, request, authentication.getName()));
    }

    @Operation(summary = "Tim kiem bai viet")
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(postService.searchPosts(keyword, page, size, email));
    }

    @Operation(summary = "Xoa bai viet")
    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {
        postService.deletePost(postId, authentication.getName());
        return ResponseEntity.ok("Xoa bai viet thanh cong!");
    }
}
