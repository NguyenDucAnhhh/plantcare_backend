package com.example.plantcare.service;

import com.example.plantcare.dto.request.GardenRequest;
import com.example.plantcare.dto.response.GardenResponse;
import java.util.List;

public interface GardenService {
    GardenResponse createGarden(GardenRequest request, String email);
    List<GardenResponse> getMyGardens(String email);
    GardenResponse updateGarden(Long gardenId, GardenRequest request, String email);
    void deleteGarden(Long gardenId, String email);
}
