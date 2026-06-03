package com.example.plantcare.controller;

import com.example.plantcare.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "Generic endpoints for uploading media files")
public class FileController {

    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload 1 ảnh", description = "Upload 1 ảnh lên Cloudinary và trả về đường link URL")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, folder);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload ảnh: !" + e.getMessage());
        }
    }

    @Operation(summary = "Upload nhiều ảnh", description = "Upload nhiều ảnh lên Cloudinary và trả về danh sách các link URL")
    @PostMapping(value = "/upload-multiple", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = cloudinaryService.uploadImage(file, folder);
                imageUrls.add(imageUrl);
            }
            return ResponseEntity.ok(imageUrls);
        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload nhiều ảnh: !" + e.getMessage());
        }
    }
}
