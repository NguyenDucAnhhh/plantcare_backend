package com.example.plantcare.dto.response;

import com.example.plantcare.model.Reminder;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
public class ReminderResponse {
    private Long id;
    private String type;
    private LocalTime triggerTime;
    private String repeatDays;

    private Long plantId;
    private LocalDateTime lastPerformed;
    private LocalDateTime nextExecution;

    public static ReminderResponse fromEntity(Reminder reminder) {
        LocalDateTime nextExec = null;
        LocalDateTime baseTime = reminder.getLastPerformed() != null ? reminder.getLastPerformed() : LocalDateTime.now();
        baseTime = LocalDateTime.of(baseTime.toLocalDate(), reminder.getTriggerTime());
        
        if (reminder.getRepeatDays() != null && reminder.getRepeatDays().contains("_")) {
            try {
                String[] parts = reminder.getRepeatDays().split("_");
                int amount = Integer.parseInt(parts[0]);
                String unit = parts[1]; // DAYS, WEEKS, MONTHS
                
                if (reminder.getLastPerformed() == null) {
                    nextExec = baseTime;
                } else {
                    switch(unit) {
                        case "DAYS": nextExec = baseTime.plusDays(amount); break;
                        case "WEEKS": nextExec = baseTime.plusWeeks(amount); break;
                        case "MONTHS": nextExec = baseTime.plusMonths(amount); break;
                        default: nextExec = baseTime;
                    }
                }
                
                // If nextExec is in the past compared to today, fast-forward it
                LocalDateTime nowTruncated = LocalDateTime.now().withSecond(0).withNano(0);
                if (nextExec.isBefore(nowTruncated)) {
                    nextExec = LocalDateTime.of(LocalDate.now(), reminder.getTriggerTime());
                    if (nextExec.isBefore(nowTruncated)) {
                         nextExec = nextExec.plusDays(1);
                    }
                }
            } catch (Exception e) {}
        }

        return ReminderResponse.builder()
                .id(reminder.getId())
                .type(reminder.getType().name())
                .triggerTime(reminder.getTriggerTime())
                .repeatDays(reminder.getRepeatDays())
                .plantId(reminder.getPlant().getId())
                .lastPerformed(reminder.getLastPerformed())
                .nextExecution(nextExec)
                .build();
    }

}
