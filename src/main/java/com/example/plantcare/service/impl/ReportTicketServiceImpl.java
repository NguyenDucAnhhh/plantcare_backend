package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.ReportTicketRequest;
import com.example.plantcare.dto.response.ReportTicketResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Post;
import com.example.plantcare.model.ReportTicket;
import com.example.plantcare.model.Role;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.PostRepository;
import com.example.plantcare.repository.ReportTicketRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.ReportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportTicketServiceImpl implements ReportTicketService {

    private final ReportTicketRepository reportTicketRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public ReportTicketResponse createTicket(ReportTicketRequest request, String email) {
        User reporter = userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("POST_NOT_FOUND", "Bài đăng không tồn tại!"));

        ReportTicket ticket = ReportTicket.builder()
                .reporter(reporter)
                .post(post)
                .reason(request.getReason())
                .status("PENDING")
                .build();

        return ReportTicketResponse.fromEntity(reportTicketRepository.save(ticket));
    }
}

