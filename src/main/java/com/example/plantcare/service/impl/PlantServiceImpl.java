package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.PlantRequest;
import com.example.plantcare.dto.response.PlantResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.Garden;
import com.example.plantcare.model.Plant;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.GardenRepository;
import com.example.plantcare.repository.PlantRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final PlantRepository plantRepository;
    private final GardenRepository gardenRepository;
    private final UserRepository userRepository;

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("USER_NOT_FOUND", "Tài khoản không tồn tại!"));
    }

    private Garden getMyGardenById(Long gardenId, User owner) {
        Garden garden = gardenRepository.findById(gardenId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("GARDEN_NOT_FOUND", "Không tìm thấy khu vườn!"));
        if (!garden.getUser().getId().equals(owner.getId())) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_GARDEN_ACCESS", "Bạn không có quyền truy cập khu vườn này!");
        }
        return garden;
    }

    private Plant getMyPlantById(Long plantId, User owner) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("PLANT_NOT_FOUND", "Không tìm thấy cây!"));
        if (!plant.getGarden().getUser().getId().equals(owner.getId())) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_PLANT_ACCESS", "Bạn không có quyền thao tác với cây này!");
        }
        return plant;
    }

    @Override
    public PlantResponse addPlantToGarden(Long gardenId, PlantRequest request, String email) {
        User owner = getUserByEmail(email);
        Garden garden = getMyGardenById(gardenId, owner);

        Plant newPlant = Plant.builder()
                .name(request.getName())
                .species(request.getSpecies())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .datePlanted(request.getDatePlanted() != null ? request.getDatePlanted() : LocalDate.now())
                .garden(garden)
                .build();

        return PlantResponse.fromEntity(plantRepository.save(newPlant));
    }

    @Override
    public List<PlantResponse> getPlantsByGarden(Long gardenId, String email) {
        User owner = getUserByEmail(email);
        Garden garden = getMyGardenById(gardenId, owner);

        return plantRepository.findByGarden(garden)
                .stream()
                .map(PlantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PlantResponse updatePlant(Long plantId, PlantRequest request, String email) {
        User owner = getUserByEmail(email);
        Plant plant = getMyPlantById(plantId, owner);

        if (request.getName() != null) plant.setName(request.getName());
        if (request.getSpecies() != null) plant.setSpecies(request.getSpecies());
        if (request.getDescription() != null) plant.setDescription(request.getDescription());
        if (request.getImageUrl() != null) plant.setImageUrl(request.getImageUrl());
        if (request.getDatePlanted() != null) plant.setDatePlanted(request.getDatePlanted());

        return PlantResponse.fromEntity(plantRepository.save(plant));
    }

    @Override
    public PlantResponse movePlant(Long plantId, Long targetGardenId, String email) {
        User owner = getUserByEmail(email);
        Plant plant = getMyPlantById(plantId, owner);
        Garden targetGarden = getMyGardenById(targetGardenId, owner);
        plant.setGarden(targetGarden);
        return PlantResponse.fromEntity(plantRepository.save(plant));
    }

    @Override
    public void deletePlant(Long plantId, String email) {
        User owner = getUserByEmail(email);
        Plant plant = getMyPlantById(plantId, owner);
        plantRepository.delete(plant);
    }
}

