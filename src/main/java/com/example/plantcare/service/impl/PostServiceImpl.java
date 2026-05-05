package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.PostRequest;
import com.example.plantcare.dto.response.PostResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public PostResponse createPost(PostRequest request, String email) {
        User author = getUserByEmail(email);

        Post newPost = Post.builder()
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .author(author)
                .isVisible(true)
                .likeCount(0)
                .build();

        return PostResponse.fromEntity(postRepository.save(newPost));
    }

    @Override
    public List<PostResponse> getAllVisiblePosts() {
        return postRepository.findAll().stream()
                .filter(Post::isVisible)
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getMyPosts(String email) {
        User author = getUserByEmail(email);
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId()).stream()
                .filter(Post::isVisible)
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết!"));
        if (!post.isVisible()) {
            throw new RuntimeException("Bài viết đã bị ẩn hoặc xóa!");
        }
        return PostResponse.fromEntity(post);
    }

    @Override
    public PostResponse updatePost(Long postId, PostRequest request, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết!"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa bài viết của người khác!");
        }

        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getImageUrl() != null) post.setImageUrl(request.getImageUrl());

        return PostResponse.fromEntity(postRepository.save(post));
    }

    @Override
    public void deletePost(Long postId, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết!"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết của người khác!");
        }

        // Thay vì xóa cứng khỏi DB (mất dữ liệu report), ta đánh cờ ẩn nó đi. Hoặc xóa thẳng nếu app cần giải phóng dung lượng.
        // Ở đây thực hiện Xóa thẳng theo Cascade.
        postRepository.delete(post);
    }
}
