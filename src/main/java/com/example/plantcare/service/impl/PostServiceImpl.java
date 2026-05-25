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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final com.example.plantcare.repository.LikeActionRepository likeActionRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    private PostResponse mapToResponse(Post post, String currentUserEmail) {
        PostResponse response = PostResponse.fromEntity(post);
        if (currentUserEmail != null) {
            response.setMine(post.getAuthor().getEmail().equals(currentUserEmail));
            response.setLiked(likeActionRepository.existsByUser_EmailAndPost(currentUserEmail, post));
        }
        return response;
    }

    @Override
    public PostResponse createPost(PostRequest request, String email) {
        User author = getUserByEmail(email);

        Post newPost = Post.builder()
                .content(request.getContent())
                .imageUrls(request.getImageUrls() != null ? request.getImageUrls() : new java.util.ArrayList<>())
                .author(author)
                .isVisible(true)
                .likeCount(0)
                .build();

        return mapToResponse(postRepository.save(newPost), email);
    }

    @Override
    public List<PostResponse> getAllVisiblePosts(String currentUserEmail) {
        return postRepository.findAll().stream()
                .filter(Post::isVisible)
                .map(post -> mapToResponse(post, currentUserEmail))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getFollowingPosts(String email) {
        User user = getUserByEmail(email);
        List<Long> followingIds = user.getFollowing().stream().map(User::getId).collect(Collectors.toList());
        if (followingIds.isEmpty()) return List.of();
        return postRepository.findByAuthorIdInOrderByCreatedAtDesc(followingIds).stream()
                .filter(Post::isVisible)
                .map(post -> mapToResponse(post, email))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getMyPosts(String email) {
        User author = getUserByEmail(email);
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(author.getId()).stream()
                .filter(Post::isVisible)
                .map(post -> mapToResponse(post, email))
                .collect(Collectors.toList());
    }

    @Override
    public PostResponse getPostById(Long postId, String currentUserEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết!"));
        if (!post.isVisible()) {
            throw new RuntimeException("Bài viết đã bị ẩn!");
        }
        return mapToResponse(post, currentUserEmail);
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
        if (request.getImageUrls() != null) post.setImageUrls(request.getImageUrls());

        return mapToResponse(postRepository.save(post), email);
    }

    @Override
    public void deletePost(Long postId, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài viết!"));

        if (!post.getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài viết của người khác!");
        }

        postRepository.delete(post);
    }
}