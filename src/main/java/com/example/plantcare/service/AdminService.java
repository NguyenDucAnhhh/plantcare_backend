package com.example.plantcare.service;

import com.example.plantcare.dto.response.DashboardStatsResponse;
import com.example.plantcare.dto.response.AdminUserResponse;
import com.example.plantcare.dto.response.AdminReportResponse;
import com.example.plantcare.dto.response.AdminPostResponse;
import org.springframework.data.domain.Page;

public interface AdminService {
    DashboardStatsResponse getDashboardStats();
    Page<AdminUserResponse> getAllUsers(int page, int size);
    void toggleUserStatus(Long userId);
    void changeUserRole(Long userId, String newRole);
    Page<AdminReportResponse> getAllReports(int page, int size);
    void resolveReport(Long reportId, String action);
    Page<AdminPostResponse> getAllPosts(int page, int size);
    void togglePostVisibility(Long postId);
}
