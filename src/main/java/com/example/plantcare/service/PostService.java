package com.example.plantcare.service;

import com.example.plantcare.dto.request.PostRequest;
import com.example.plantcare.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest request, String email);
    List<PostResponse> getAllVisiblePosts(String currentUserEmail);
    List<PostResponse> getFollowingPosts(String email);
    List<PostResponse> getMyPosts(String email);
    PostResponse getPostById(Long postId, String currentUserEmail);
    PostResponse updatePost(Long postId, PostRequest request, String email);
    void deletePost(Long postId, String email);
}
