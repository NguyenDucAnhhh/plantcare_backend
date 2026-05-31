package com.example.plantcare.service.impl;

import com.example.plantcare.dto.response.DashboardStatsResponse;
import com.example.plantcare.dto.response.AdminUserResponse;
import com.example.plantcare.dto.response.AdminReportResponse;
import com.example.plantcare.dto.response.AdminPostResponse;
import com.example.plantcare.repository.CareTipRepository;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.ReportTicketRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.AdminService;
import com.example.plantcare.model.User;
import com.example.plantcare.model.Role;
import com.example.plantcare.model.ReportTicket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportTicketRepository reportTicketRepository;
    private final CareTipRepository careTipRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        return DashboardStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalPosts(postRepository.count())
                .totalReports(reportTicketRepository.count())
                .totalTips(careTipRepository.count())
                .build();
    }

    @Override
    public Page<AdminUserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(pageable).map(user -> AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .postCount(postRepository.countByAuthorId(user.getId()))
                .build());
    }

    @Override
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public void changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.valueOf(newRole.toUpperCase()));
        userRepository.save(user);
    }

    @Override
    public Page<AdminReportResponse> getAllReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reportTicketRepository.findAll(pageable).map(ticket -> AdminReportResponse.builder()
                .id(ticket.getId())
                .reporterName(ticket.getReporter().getFullName())
                .reporterEmail(ticket.getReporter().getEmail())
                .reason(ticket.getReason())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .postId(ticket.getPost().getId())
                .postContent(ticket.getPost().getContent())
                .postAuthorId(ticket.getPost().getAuthor().getId())
                .postAuthorName(ticket.getPost().getAuthor().getFullName())
                .postAuthorAvatar(ticket.getPost().getAuthor().getAvatarUrl())
                .postImageUrls(ticket.getPost().getImageUrls() != null ? new ArrayList<>(ticket.getPost().getImageUrls()) : new ArrayList<>())
                .postLikeCount(ticket.getPost().getLikeCount())
                .postCommentCount(ticket.getPost().getComments() != null ? ticket.getPost().getComments().size() : 0)
                .postCreatedAt(ticket.getPost().getCreatedAt())
                .build());
    }

        @Override
    public void resolveReport(Long reportId, String action) {
        com.example.plantcare.model.ReportTicket ticket = reportTicketRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if ("DELETE_POST".equalsIgnoreCase(action)) {
            ticket.setStatus("DELETED");
            ticket.getPost().setVisible(false);
            postRepository.save(ticket.getPost());
        } else if ("KEEP_POST".equalsIgnoreCase(action) || "RESTORE_POST".equalsIgnoreCase(action)) {
            ticket.setStatus("KEPT");
            ticket.getPost().setVisible(true);
            postRepository.save(ticket.getPost());
        }
        reportTicketRepository.save(ticket);
    }

    @Override
    public Page<AdminPostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAll(pageable).map(post -> AdminPostResponse.builder()
                        .id(post.getId())
                        .authorName(post.getAuthor().getFullName())
                        .authorEmail(post.getAuthor().getEmail())
                        .content(post.getContent())
                        .imageUrls(post.getImageUrls())
                        .isVisible(post.isVisible())
                        .likeCount(post.getLikeCount())
                        .createdAt(post.getCreatedAt())
                        .build());
    }

    @Override
    public void togglePostVisibility(Long postId) {
        com.example.plantcare.model.Post post = postRepository.findById(postId)
                .orElseThrow(() -> new com.example.plantcare.exception.ResourceNotFoundException("Khong tim thay bai dang"));

        boolean newVisibility = !post.isVisible();
        post.setVisible(newVisibility);
        postRepository.save(post);

        List<ReportTicket> tickets = reportTicketRepository.findByPostId(postId);
        if (tickets != null && !tickets.isEmpty()) {
            for (ReportTicket ticket : tickets) {
                ticket.setStatus(newVisibility ? "KEPT" : "DELETED");
                reportTicketRepository.save(ticket);
            }
        }
    }
}


