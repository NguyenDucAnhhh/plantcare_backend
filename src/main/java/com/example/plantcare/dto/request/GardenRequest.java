package com.example.plantcare.dto.request;
import lombok.Data;
@Data
public class GardenRequest {
    private String name;
    private String location;
    private String description;
    private String imageUrl;
}
