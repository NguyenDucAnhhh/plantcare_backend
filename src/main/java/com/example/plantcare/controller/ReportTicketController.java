package com.example.plantcare.controller;

import com.example.plantcare.dto.request.ReportTicketRequest;
import com.example.plantcare.dto.response.ReportTicketResponse;
import com.example.plantcare.service.ReportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "12. Tòa Án Tối Cao (ADMIN)", description = "Nơi giải quyết khiếu nại, report bài viết xấu")
public class ReportTicketController {

    private final ReportTicketService reportTicketService;

    @Operation(summary = "Gửi 1 đơn tố cáo", description = "Dành cho Người Dùng báo cáo bài viết vi phạm")
    @PostMapping
    public ResponseEntity<ReportTicketResponse> createTicket(
            @RequestBody ReportTicketRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reportTicketService.createTicket(request, authentication.getName()));
    }

    @Operation(summary = "Xem toàn bộ đơn tố cáo", description = "CHỈ ADMIN")
    @GetMapping
    public ResponseEntity<List<ReportTicketResponse>> getAllTickets(Authentication authentication) {
        return ResponseEntity.ok(reportTicketService.getAllTickets(authentication.getName()));
    }

    @Operation(summary = "Giải quyết đơn tố cáo", description = "CHỈ ADMIN. Action: 'KEPT' (Giữ bài) hoặc 'DELETED' (Xóa bài)")
    @PatchMapping("/{ticketId}/resolve")
    public ResponseEntity<String> resolveTicket(
            @PathVariable Long ticketId,
            @RequestParam String action,
            Authentication authentication) {
        reportTicketService.resolveTicket(ticketId, action, authentication.getName());
        return ResponseEntity.ok("Đã xử lý đơn tố cáo với phán quyết: " + action);
    }
}
