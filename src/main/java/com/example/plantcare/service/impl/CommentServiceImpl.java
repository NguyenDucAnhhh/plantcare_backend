package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.CommentRequest;
import com.example.plantcare.dto.response.CommentResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Comment;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.CommentRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    @Override
    public CommentResponse addComment(Long postId, CommentRequest request, String email) {
        User author = getUserByEmail(email);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại!"));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bình luận gốc không tồn tại!"));
        }

        Comment newComment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .parentComment(parentComment)
                .build();

        return CommentResponse.fromEntity(commentRepository.save(newComment));
    }

    @Override
    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại!"));

        return post.getComments().stream()
                .map(CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId, String email) {
        User author = getUserByEmail(email);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận không tồn tại!"));

        // Chỉ tác giả bài viết hoặc tác giả bình luận mới được xóa
        if (!comment.getAuthor().getId().equals(author.getId()) && !comment.getPost().getAuthor().getId().equals(author.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này!");
        }

        commentRepository.delete(comment);
    }
}
