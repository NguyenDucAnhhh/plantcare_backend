package com.example.plantcare.dto.request;

import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
public class ReminderRequest {
    private String type;
    private LocalTime triggerTime;
    private String repeatDays;
    private LocalDateTime lastPerformed;
}
