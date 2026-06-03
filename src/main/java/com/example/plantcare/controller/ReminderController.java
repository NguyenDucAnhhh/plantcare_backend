package com.example.plantcare.controller;

import com.example.plantcare.dto.request.ReminderRequest;
import com.example.plantcare.dto.response.ReminderResponse;
import com.example.plantcare.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Quản Lý Báo Thức", description = "Đặt lịch chăm sóc (tưới nước, bón phân) cho Cây")
public class ReminderController {

    private final ReminderService reminderService;

    @Operation(summary = "Tạo báo thức mới cho 1 Cây")
    @PostMapping("/plants/{plantId}/reminders")
    public ResponseEntity<ReminderResponse> createReminder(
            @PathVariable Long plantId,
            @RequestBody ReminderRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reminderService.createReminder(plantId, request, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ báo thức của 1 vườn")
    @GetMapping("/gardens/{gardenId}/reminders")
    public ResponseEntity<List<ReminderResponse>> getRemindersByGarden(
            @PathVariable Long gardenId,
            Authentication authentication) {
        return ResponseEntity.ok(reminderService.getRemindersByGarden(gardenId, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ báo thức của 1 cây")
    @GetMapping("/plants/{plantId}/reminders")
    public ResponseEntity<List<ReminderResponse>> getRemindersByPlant(
            @PathVariable Long plantId,
            Authentication authentication) {
        return ResponseEntity.ok(reminderService.getRemindersByPlant(plantId, authentication.getName()));
    }

    @Operation(summary = "Cập nhật ngày, giờ báo thức")
    @PutMapping("/reminders/{reminderId}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long reminderId,
            @RequestBody ReminderRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reminderService.updateReminder(reminderId, request, authentication.getName()));
    }


    @Operation(summary = "Xóa vĩnh viễn báo thức")
    @DeleteMapping("/reminders/{reminderId}")
    public ResponseEntity<String> deleteReminder(
            @PathVariable Long reminderId,
            Authentication authentication) {
        reminderService.deleteReminder(reminderId, authentication.getName());
        return ResponseEntity.ok("Đã xóa báo thức thành công!");
    }
}