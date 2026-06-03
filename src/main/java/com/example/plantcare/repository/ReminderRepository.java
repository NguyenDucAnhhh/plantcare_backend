package com.example.plantcare.repository;

import com.example.plantcare.model.Reminder;
import com.example.plantcare.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalTime;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByPlant(Plant plant);
    List<Reminder> findByPlant_Garden_Id(Long gardenId);
    List<Reminder> findByTriggerTime(LocalTime time);
}