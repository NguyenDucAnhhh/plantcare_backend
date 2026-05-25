package com.example.plantcare.controller;

import com.example.plantcare.dto.request.PlantRequest;
import com.example.plantcare.dto.response.PlantResponse;
import com.example.plantcare.service.PlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.plantcare.service.CloudinaryService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "3. Quản Lý Cây Trồng", description = "CRUD Cây bên trong Vườn")
public class PlantController {

    private final PlantService plantService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload ảnh cho cây")
    @PostMapping(value = "/plants/image/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadPlantImage(
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, "plants");
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi upload ảnh cây: " + e.getMessage());
        }
    }

    @PostMapping("/gardens/{gardenId}/plants")
    public ResponseEntity<PlantResponse> addPlantToGarden(
            @PathVariable Long gardenId,
            @RequestBody PlantRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(plantService.addPlantToGarden(gardenId, request, authentication.getName()));
    }

    @Operation(summary = "Lấy toàn bộ Cây trong Vườn")
    @GetMapping("/gardens/{gardenId}/plants")
    public ResponseEntity<List<PlantResponse>> getPlantsByGarden(
            @PathVariable Long gardenId,
            Authentication authentication) {
        return ResponseEntity.ok(plantService.getPlantsByGarden(gardenId, authentication.getName()));
    }

    @Operation(summary = "Cập nhật thông tin Cây")
    @PutMapping("/plants/{plantId}")
    public ResponseEntity<PlantResponse> updatePlant(
            @PathVariable Long plantId,
            @RequestBody PlantRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(plantService.updatePlant(plantId, request, authentication.getName()));
    }

    @Operation(summary = "Di chuyển Cây sang Vườn khác")
    @PutMapping("/plants/{plantId}/move/{targetGardenId}")
    public ResponseEntity<PlantResponse> movePlant(
            @PathVariable Long plantId,
            @PathVariable Long targetGardenId,
            Authentication authentication) {
        return ResponseEntity.ok(plantService.movePlant(plantId, targetGardenId, authentication.getName()));
    }

    @Operation(summary = "Xóa Cây (Kéo theo xóa luôn Nhắc nhở)")
    @DeleteMapping("/plants/{plantId}")
    public ResponseEntity<String> deletePlant(
            @PathVariable Long plantId,
            Authentication authentication) {
        plantService.deletePlant(plantId, authentication.getName());
        return ResponseEntity.ok("Xóa cây thành công!");
    }
}
