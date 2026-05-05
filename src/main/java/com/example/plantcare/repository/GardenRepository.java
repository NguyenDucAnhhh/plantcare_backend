package com.example.plantcare.repository;
import com.example.plantcare.model.Garden;
import com.example.plantcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GardenRepository extends JpaRepository<Garden, Long> {
    List<Garden> findByUser(User user);
}
