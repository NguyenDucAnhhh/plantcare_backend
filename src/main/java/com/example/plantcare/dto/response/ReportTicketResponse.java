package com.example.plantcare.dto.response;

import com.example.plantcare.model.ReportTicket;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportTicketResponse {
    private Long id;
    private String reporterName;
    private Long postId;
    private String reason;
    private String status;
    private LocalDateTime createdAt;

    public static ReportTicketResponse fromEntity(ReportTicket ticket) {
        return ReportTicketResponse.builder()
                .id(ticket.getId())
                .reporterName(ticket.getReporter().getFullName())
                .postId(ticket.getPost().getId())
                .reason(ticket.getReason())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
