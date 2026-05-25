package com.example.plantcare.service;

import com.example.plantcare.dto.request.PlantRequest;
import com.example.plantcare.dto.response.PlantResponse;

import java.util.List;

public interface PlantService {
    PlantResponse addPlantToGarden(Long gardenId, PlantRequest request, String email);
    List<PlantResponse> getPlantsByGarden(Long gardenId, String email);
    PlantResponse updatePlant(Long plantId, PlantRequest request, String email);
    void deletePlant(Long plantId, String email);
    PlantResponse movePlant(Long plantId, Long targetGardenId, String email);
}
