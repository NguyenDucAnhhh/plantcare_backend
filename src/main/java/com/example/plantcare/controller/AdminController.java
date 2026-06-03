package com.example.plantcare.controller;

import com.example.plantcare.dto.response.DashboardStatsResponse;
import com.example.plantcare.dto.response.AdminUserResponse;
import com.example.plantcare.dto.response.AdminReportResponse;
import com.example.plantcare.service.AdminService;
import com.example.plantcare.service.DiagnosisHistoryService;
import com.example.plantcare.service.CareTipService;
import com.example.plantcare.dto.response.CareTipResponse;
import com.example.plantcare.dto.request.DiagnosisEvaluateRequest;
import com.example.plantcare.dto.response.DiagnosisHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final CareTipService careTipService;
    private final DiagnosisHistoryService diagnosisHistoryService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size));
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<Void> toggleUserStatus(@PathVariable Long id) {
        adminService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id, @RequestParam String role) {
        adminService.changeUserRole(id, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<AdminReportResponse>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllReports(page, size));
    }

    @PutMapping("/reports/{id}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable Long id, @RequestParam String action) {
        adminService.resolveReport(id, action);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/diagnoses")
    public ResponseEntity<Page<DiagnosisHistoryResponse>> getAllDiagnoses(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String confidence,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(diagnosisHistoryService.getAllDiagnoses(status, rating, confidence, search, pageable));
    }

    @PatchMapping("/diagnoses/{id}/evaluate")
    public ResponseEntity<DiagnosisHistoryResponse> evaluateDiagnosis(
            @PathVariable Long id,
            @RequestBody DiagnosisEvaluateRequest request) {
        return ResponseEntity.ok(diagnosisHistoryService.evaluateDiagnosis(id, request));
    }
    @GetMapping("/posts")
    public ResponseEntity<Page<com.example.plantcare.dto.response.AdminPostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminService.getAllPosts(page, size));
    }

    @PutMapping("/posts/{id}/toggle-visibility")
    public ResponseEntity<Void> togglePostVisibility(@PathVariable Long id) {
        adminService.togglePostVisibility(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tips")
    public ResponseEntity<Page<CareTipResponse>> getAdminCareTips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(careTipService.getAdminCareTips(page, size));
    }
}
