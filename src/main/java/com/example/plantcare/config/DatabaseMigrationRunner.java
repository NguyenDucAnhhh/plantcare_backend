package com.example.plantcare.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigrationRunner implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            log.info("Bắt đầu kiểm tra và dọn dẹp Database (Render)...");
            jdbcTemplate.execute("ALTER TABLE reminders DROP COLUMN IF EXISTS is_active;");
            log.info("✅ Đã dọn dẹp thành công: Xóa cột is_active khỏi bảng reminders.");
        } catch (Exception e) {
            log.warn("⚠️ Bỏ qua dọn dẹp: Cột is_active có thể đã được xóa hoặc không có quyền tác động.");
        }
    }
}
