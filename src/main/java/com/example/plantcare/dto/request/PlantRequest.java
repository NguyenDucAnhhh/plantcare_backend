package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class PlantRequest {
    private String name;
    private String species;
    private String description;
    private String imageUrl;
}
