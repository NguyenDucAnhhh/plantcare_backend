package com.example.plantcare.repository;
import com.example.plantcare.model.Reminder;
import com.example.plantcare.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByPlant(Plant plant);
}
