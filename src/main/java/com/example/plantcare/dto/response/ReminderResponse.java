package com.example.plantcare.dto.response;

import com.example.plantcare.model.Reminder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class ReminderResponse {
    private Long id;
    private String type;
    private LocalTime triggerTime;
    private String repeatDays;
    private boolean isActive;
    private Long plantId;

    public static ReminderResponse fromEntity(Reminder reminder) {
        return ReminderResponse.builder()
                .id(reminder.getId())
                .type(reminder.getType().name())
                .triggerTime(reminder.getTriggerTime())
                .repeatDays(reminder.getRepeatDays())
                .isActive(reminder.isActive())
                .plantId(reminder.getPlant().getId())
                .build();
    }
}
