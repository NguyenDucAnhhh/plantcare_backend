package com.example.plantcare.controller;

import com.example.plantcare.dto.request.CareTipRequest;
import com.example.plantcare.dto.response.CareTipResponse;
import com.example.plantcare.service.CareTipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/care-tips")
@RequiredArgsConstructor
@Tag(name = "11. Cẩm Nang Chăm Sóc (ADMIN)", description = "Quản trị viên đăng bài chia sẻ kiến thức")
public class CareTipController {

    private final CareTipService careTipService;

    @Operation(summary = "Xem toàn bộ cẩm nang", description = "Dành cho TẤT CẢ mọi người")
    @GetMapping
    public ResponseEntity<List<CareTipResponse>> getAllCareTips() {
        return ResponseEntity.ok(careTipService.getAllCareTips());
    }

    @Operation(summary = "Xem chi tiết 1 bài cẩm nang")
    @GetMapping("/{id}")
    public ResponseEntity<CareTipResponse> getCareTipById(@PathVariable Long id) {
        return ResponseEntity.ok(careTipService.getCareTipById(id));
    }

    @Operation(summary = "Đăng cẩm nang mới", description = "CHỈ ADMIN (Nếu User bình thường gọi sẽ bị lỗi)")
    @PostMapping
    public ResponseEntity<CareTipResponse> createCareTip(
            @RequestBody CareTipRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(careTipService.createCareTip(request, authentication.getName()));
    }

    @Operation(summary = "Sửa bài cẩm nang", description = "CHỈ ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<CareTipResponse> updateCareTip(
            @PathVariable Long id,
            @RequestBody CareTipRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(careTipService.updateCareTip(id, request, authentication.getName()));
    }

    @Operation(summary = "Xóa bài cẩm nang", description = "CHỈ ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCareTip(
            @PathVariable Long id,
            Authentication authentication) {
        careTipService.deleteCareTip(id, authentication.getName());
        return ResponseEntity.ok("Xóa cẩm nang thành công!");
    }
}
