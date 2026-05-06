package com.example.plantcare.dto.response;

import com.example.plantcare.model.Garden;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GardenResponse {
    private Long id;
    private String name;
    private String location;
    private String description;
    private String imageUrl;
    private int plantCount;

    public static GardenResponse fromEntity(Garden garden) {
        return GardenResponse.builder()
                .id(garden.getId())
                .name(garden.getName())
                .location(garden.getLocation())
                .description(garden.getDescription())
                .imageUrl(garden.getImageUrl())
                .plantCount(garden.getPlants() != null ? garden.getPlants().size() : 0)
                .build();
    }
}