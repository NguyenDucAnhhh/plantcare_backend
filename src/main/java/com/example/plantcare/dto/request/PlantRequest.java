package com.example.plantcare.dto.request;
import lombok.Data;
import java.time.LocalDate;
@Data
public class PlantRequest {
    private String name;
    private String species;
    private String description;
    private String imageUrl;
    private LocalDate datePlanted;
}
