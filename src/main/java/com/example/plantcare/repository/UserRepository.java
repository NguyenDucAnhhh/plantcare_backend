package com.example.plantcare.repository;

import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

// Háº§m ngáº§m Data khai thÃ¡c riÃªng cho cÃ¡i Báº£ng Users
// Viá»‡c Extend `JpaRepository` giÃºp láº¥y luÃ´n máº¥y hÃ m CRUD (Save, Find, Delete) cÃ³ sáºµn cá»§a CSDl
public interface UserRepository extends JpaRepository<User, Long> {
    
    // CÃ¢u lá»‡nh SQL tÃ ng hÃ¬nh: SELECT * FROM Users WHERE email = ?
    Optional<User> findByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.following f WHERE f.id = :userId")
    int countFollowers(@Param("userId") Long userId);
}
