package com.example.plantcare.dto.request;

import lombok.Data;

@Data
public class ReportTicketRequest {
    private Long postId;
    private String reason;
}
