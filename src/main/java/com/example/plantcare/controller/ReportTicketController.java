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

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Tố cáo bài đăng", description = "Quan ly Bai viet")
public class ReportTicketController {

    private final ReportTicketService reportTicketService;

    @Operation(summary = "Gui don to cao bai viet", description = "Nguoi dung bao cao bai viet vi pham")
    @PostMapping
    public ResponseEntity<ReportTicketResponse> createTicket(
            @RequestBody ReportTicketRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reportTicketService.createTicket(request, authentication.getName()));
    }
}