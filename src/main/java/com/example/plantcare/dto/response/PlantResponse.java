package com.example.plantcare.dto.response;

import com.example.plantcare.model.Plant;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PlantResponse {
    private Long id;
    private String name;
    private String species;
    private String description;
    private String imageUrl;
    private LocalDate datePlanted;
    private Long gardenId;

    public static PlantResponse fromEntity(Plant plant) {
        return PlantResponse.builder()
                .id(plant.getId())
                .name(plant.getName())
                .species(plant.getSpecies())
                .description(plant.getDescription())
                .imageUrl(plant.getImageUrl())
                .datePlanted(plant.getDatePlanted())
                .gardenId(plant.getGarden().getId())
                .build();
    }
}
