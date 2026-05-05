package com.example.plantcare.service;

import com.example.plantcare.dto.request.CareTipRequest;
import com.example.plantcare.dto.response.CareTipResponse;

import java.util.List;

public interface CareTipService {
    // Cho TẤT CẢ mọi người
    List<CareTipResponse> getAllCareTips();
    CareTipResponse getCareTipById(Long id);

    // Chỉ dành cho ADMIN
    CareTipResponse createCareTip(CareTipRequest request, String email);
    CareTipResponse updateCareTip(Long id, CareTipRequest request, String email);
    void deleteCareTip(Long id, String email);
}
