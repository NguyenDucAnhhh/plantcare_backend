package com.example.plantcare.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * TANG SERVICE - CLOUDINARY
 * =========================
 * - Inject (Tiem) Bean Cloudinary da duoc khoi tao tu CloudinaryConfig.
 * - Ham uploadImage(): Nhan file anh tho tu Controller, day len Cloudinary,
 *   nhan ve URL cong khai (public URL) de luu vao Database.
 * - Ham deleteImage(): Xoa anh khoi Cloudinary theo publicId (khi User doi anh).
 *
 * HOW IT WORKS (Luong chay thuc te):
 *   Flutter chup anh -> Gui MultipartFile len API Spring Boot
 *   -> CloudinaryService.uploadImage() doi anh len Cloudinary
 *   -> Lay ve URL anh (https://res.cloudinary.com/...)
 *   -> Luu URL do vao cot imageUrl trong Database
 *   -> Tra URL ve cho Flutter hien thi
 */
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    // Tiem Bean tu CloudinaryConfig - KHONG can biet API Key la gi!
    private final Cloudinary cloudinary;

    /**
     * Upload anh len Cloudinary.
     * @param file  File anh nhan tu Flutter (MultipartFile)
     * @param folder Thu muc tren Cloudinary (vi du: "avatars", "posts", "plants")
     * @return URL cong khai cua anh sau khi upload (String)
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,          // Phan loai thu muc
                        "resource_type", "auto"    // Tu dong nhan dang dinh dang
                )
        );
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Xoa anh khoi Cloudinary (Dung khi User doi avatar, xoa bai viet...).
     * @param publicId ID cua anh tren Cloudinary (lay tu URL)
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
