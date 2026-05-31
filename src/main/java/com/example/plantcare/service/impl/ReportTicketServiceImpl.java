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
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại!"));

        ReportTicket ticket = ReportTicket.builder()
                .reporter(reporter)
                .post(post)
                .reason(request.getReason())
                .status("PENDING")
                .build();

        return ReportTicketResponse.fromEntity(reportTicketRepository.save(ticket));
    }

    @Override
    public List<ReportTicketResponse> getAllTickets(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        if (admin.getRole() != Role.ADMIN) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_ADMIN_ONLY", "Chỉ ADMIN mới được xem danh sách tố cáo!");
        }

        return reportTicketRepository.findAll().stream()
                .map(ReportTicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void resolveTicket(Long ticketId, String action, String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        if (admin.getRole() != Role.ADMIN) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_ADMIN_ONLY", "Chỉ ADMIN mới được giải quyết tố cáo!");
        }

        ReportTicket ticket = reportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn tố cáo không tồn tại!"));

        if (action.equalsIgnoreCase("DELETED")) {
            // Xóa bài viết hoặc ẩn bài viết
            Post post = ticket.getPost();
            post.setVisible(false); // Ẩn đi để giữ lại chứng cứ nếu cần, hoặc gọi postRepository.delete(post)
            postRepository.save(post);
            ticket.setStatus("DELETED");
        } else if (action.equalsIgnoreCase("KEPT")) {
            ticket.setStatus("KEPT");
        } else {
            throw new RuntimeException("Hành động không hợp lệ! (Chỉ chấp nhận DELETED hoặc KEPT)");
        }

        reportTicketRepository.save(ticket);
    }
}

