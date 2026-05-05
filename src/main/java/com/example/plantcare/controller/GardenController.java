package com.example.plantcare.controller;

import com.example.plantcare.dto.request.GardenRequest;
import com.example.plantcare.dto.response.GardenResponse;
import com.example.plantcare.service.GardenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gardens")
@RequiredArgsConstructor
@Tag(name = "2. Quản Lý Vườn", description = "CRUD Vườn cây cá nhân")
public class GardenController {

    private final GardenService gardenService;

    @Operation(summary = "Tạo vườn mới", description = "Bắt buộc có Token")
    @PostMapping
    public ResponseEntity<GardenResponse> createGarden(
            @RequestBody GardenRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(gardenService.createGarden(request, authentication.getName()));
    }

    @Operation(summary = "Lấy danh sách vườn", description = "Lấy toàn bộ vườn của User đang đăng nhập")
    @GetMapping
    public ResponseEntity<List<GardenResponse>> getMyGardens(Authentication authentication) {
        return ResponseEntity.ok(gardenService.getMyGardens(authentication.getName()));
    }

    @Operation(summary = "Cập nhật thông tin vườn")
    @PutMapping("/{id}")
    public ResponseEntity<GardenResponse> updateGarden(
            @PathVariable Long id,
            @RequestBody GardenRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(gardenService.updateGarden(id, request, authentication.getName()));
    }

    @Operation(summary = "Xóa vườn và toàn bộ cây bên trong")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGarden(
            @PathVariable Long id,
            Authentication authentication) {
        gardenService.deleteGarden(id, authentication.getName());
        return ResponseEntity.ok("Xóa vườn thành công!");
    }
}
