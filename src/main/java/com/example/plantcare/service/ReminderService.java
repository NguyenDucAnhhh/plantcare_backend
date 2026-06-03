package com.example.plantcare.service;

import com.example.plantcare.dto.request.ReminderRequest;
import com.example.plantcare.dto.response.ReminderResponse;

import java.util.List;

public interface ReminderService {
    ReminderResponse createReminder(Long plantId, ReminderRequest request, String email);
    List<ReminderResponse> getRemindersByPlant(Long plantId, String email);
    List<ReminderResponse> getRemindersByGarden(Long gardenId, String email);
    ReminderResponse updateReminder(Long reminderId, ReminderRequest request, String email);
    void deleteReminder(Long reminderId, String email);
}
