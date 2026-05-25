package com.example.plantcare.repository;

import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

// Hầm ngầm Data khai thác riêng cho cái Bảng Users
// Việc Extend `JpaRepository` giúp lấy luôn mấy hàm CRUD (Save, Find, Delete) có sẵn của CSDl
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Câu lệnh SQL tàng hình: SELECT * FROM Users WHERE email = ?
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.following f WHERE f.id = :userId")
    int countFollowers(@Param("userId") Long userId);
}
