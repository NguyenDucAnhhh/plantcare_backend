package com.example.plantcare.service;

import com.example.plantcare.dto.request.ReportTicketRequest;
import com.example.plantcare.dto.response.ReportTicketResponse;


public interface ReportTicketService {
    // Dành cho User
    ReportTicketResponse createTicket(ReportTicketRequest request, String email);
}
