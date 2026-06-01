package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.GardenRequest;
import com.example.plantcare.dto.response.GardenResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Garden;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.GardenRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.GardenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GardenServiceImpl implements GardenService {

    private final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
    }

    private Garden getMyGardenById(Long gardenId, User owner) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("GARDEN_NOT_FOUND", "Không tìm thấy khu vườn này!"));
        
        if (!garden.getUser().getId().equals(owner.getId())) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_GARDEN_ACCESS", "Bạn không có quyền thao tác trên khu vườn của người khác!");
        }
        return garden;
    }

    @Override
    public GardenResponse createGarden(GardenRequest request, String email) {
        User owner = getUserByEmail(email);

        Garden newGarden = Garden.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .location(request.getLocation())
                .user(owner)
                .build();

        Garden savedGarden = gardenRepository.save(newGarden);
        return GardenResponse.fromEntity(savedGarden);
    }

    @Override
    public List<GardenResponse> getMyGardens(String email) {
        User owner = getUserByEmail(email);
        return gardenRepository.findByUser(owner)
                .stream()
                .map(GardenResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public GardenResponse updateGarden(Long gardenId, GardenRequest request, String email) {
        User owner = getUserByEmail(email);
        Garden garden = getMyGardenById(gardenId, owner);

        if (request.getName() != null) garden.setName(request.getName());
        if (request.getLocation() != null) garden.setLocation(request.getLocation());
        if (request.getDescription() != null) garden.setDescription(request.getDescription());
        if (request.getImageUrl() != null) garden.setImageUrl(request.getImageUrl());

        return GardenResponse.fromEntity(gardenRepository.save(garden));
    }

    @Override
    public void deleteGarden(Long gardenId, String email) {
        User owner = getUserByEmail(email);
        Garden garden = getMyGardenById(gardenId, owner);
        gardenRepository.delete(garden);
    }
}

