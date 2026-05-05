package com.example.plantcare.service.impl;

import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.LikeAction;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.LikeActionRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeActionRepository likeActionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public boolean toggleLike(Long postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại!"));

        // Tìm xem user này đã thả tim bài này chưa (Yêu cầu repo có hàm findByUserAndPost)
        Optional<LikeAction> existingLike = likeActionRepository.findByUserAndPost(user, post);

        if (existingLike.isPresent()) {
            // Đã like rồi -> Bấm lần nữa là Hủy Like
            likeActionRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            return false; // Trạng thái hiện tại: Chưa Like
        } else {
            // Chưa like -> Tạo Like mới
            LikeAction newLike = LikeAction.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeActionRepository.save(newLike);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            return true; // Trạng thái hiện tại: Đã Like
        }
    }
}
