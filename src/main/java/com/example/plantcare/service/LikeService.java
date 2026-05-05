package com.example.plantcare.service;

public interface LikeService {
    // Trả về true nếu người dùng vừa bấm Thả Tim, false nếu người dùng vừa Rút Tim lại
    boolean toggleLike(Long postId, String email);
}
