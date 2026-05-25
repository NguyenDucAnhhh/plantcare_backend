package com.example.plantcare.service;

import com.example.plantcare.dto.request.CommentRequest;
import com.example.plantcare.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long postId, CommentRequest request, String email);
    List<CommentResponse> getCommentsByPost(Long postId, String currentUserEmail);
    void deleteComment(Long commentId, String email);
}
