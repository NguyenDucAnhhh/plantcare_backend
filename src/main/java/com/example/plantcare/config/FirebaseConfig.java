package com.example.plantcare.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * TANG CONFIG - FIREBASE
 * ======================
 * - @PostConstruct: Ham nay tu dong chay SAU KHI Spring khoi tao xong Bean nay.
 * - Doc file JSON chung chi tu thu muc resources.
 * - Khoi tao FirebaseApp - "Canh cong" de Spring Boot noi vao he thong Firebase.
 * - Chi khoi tao 1 lan, tranh loi "FirebaseApp da ton tai".
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path}")
    private String serviceAccountPath;

    @PostConstruct
    public void initFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount;
                
                // Nếu đường dẫn bắt đầu bằng "/" (như /etc/secrets trên Render) hoặc "C:" -> Đọc file trực tiếp từ ổ cứng
                if (serviceAccountPath.startsWith("/") || serviceAccountPath.contains(":\\")) {
                    java.io.File file = new java.io.File(serviceAccountPath);
                    if (!file.exists()) {
                        System.out.println("[WARN] Firebase: Khong tim thay file " + serviceAccountPath + ". Push Notification se bi tat.");
                        return;
                    }
                    serviceAccount = new java.io.FileInputStream(file);
                } else {
                    // Môi trường Local: Đọc từ thư mục resources
                    serviceAccount = getClass().getClassLoader().getResourceAsStream(serviceAccountPath);
                }

                if (serviceAccount == null) {
                    System.out.println("[WARN] Firebase: Khong tim thay file " + serviceAccountPath + " trong resources. Push Notification se bi tat.");
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("[INFO] Firebase da ket noi thanh cong!");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Firebase khoi tao that bai: " + e.getMessage());
        }
    }
}
