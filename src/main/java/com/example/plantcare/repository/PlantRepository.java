package com.example.plantcare.repository;
import com.example.plantcare.model.Plant;
import com.example.plantcare.model.Garden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    List<Plant> findByGarden(Garden garden);
}
