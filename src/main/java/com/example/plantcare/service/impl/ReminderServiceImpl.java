package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.ReminderRequest;
import com.example.plantcare.dto.response.ReminderResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Plant;
import com.example.plantcare.model.Reminder;
import com.example.plantcare.model.ReminderType;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.PlantRepository;
import com.example.plantcare.repository.ReminderRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final PlantRepository plantRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
    }

    private Plant getMyPlantById(Long plantId, User owner) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cây!"));
        if (!plant.getGarden().getUser().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền truy cập cây này!");
        }
        return plant;
    }

    private Reminder getMyReminderById(Long reminderId, User owner) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo thức!"));
        if (!reminder.getPlant().getGarden().getUser().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa báo thức này!");
        }
        return reminder;
    }

    @Override
    public ReminderResponse createReminder(Long plantId, ReminderRequest request, String email) {
        User owner = getUserByEmail(email);
        Plant plant = getMyPlantById(plantId, owner);

        Reminder newReminder = Reminder.builder()
                .type(ReminderType.valueOf(request.getType().toUpperCase()))
                .triggerTime(request.getTriggerTime())
                .repeatDays(request.getRepeatDays())
                .isActive(true) // Luôn bật khi mới tạo
                .plant(plant)
                .build();

        return ReminderResponse.fromEntity(reminderRepository.save(newReminder));
    }

    @Override
    public List<ReminderResponse> getRemindersByPlant(Long plantId, String email) {
        User owner = getUserByEmail(email);
        Plant plant = getMyPlantById(plantId, owner);

        return reminderRepository.findByPlant(plant)
                .stream()
                .map(ReminderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ReminderResponse updateReminder(Long reminderId, ReminderRequest request, String email) {
        User owner = getUserByEmail(email);
        Reminder reminder = getMyReminderById(reminderId, owner);

        if (request.getType() != null) reminder.setType(ReminderType.valueOf(request.getType().toUpperCase()));
        if (request.getTriggerTime() != null) reminder.setTriggerTime(request.getTriggerTime());
        if (request.getRepeatDays() != null) reminder.setRepeatDays(request.getRepeatDays());

        return ReminderResponse.fromEntity(reminderRepository.save(reminder));
    }

    @Override
    public ReminderResponse toggleReminderStatus(Long reminderId, String email) {
        User owner = getUserByEmail(email);
        Reminder reminder = getMyReminderById(reminderId, owner);

        // Bật/tắt công tắc
        reminder.setActive(!reminder.isActive());
        
        return ReminderResponse.fromEntity(reminderRepository.save(reminder));
    }

    @Override
    public void deleteReminder(Long reminderId, String email) {
        User owner = getUserByEmail(email);
        Reminder reminder = getMyReminderById(reminderId, owner);
        reminderRepository.delete(reminder);
    }
}
