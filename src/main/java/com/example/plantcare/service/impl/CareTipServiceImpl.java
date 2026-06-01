package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.CareTipRequest;
import com.example.plantcare.dto.response.CareTipResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.CareTip;
import com.example.plantcare.repository.CareTipRepository;
import com.example.plantcare.service.CareTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class CareTipServiceImpl implements CareTipService {

    private final CareTipRepository careTipRepository;

    @Override
    public List<CareTipResponse> getAllCareTips() {
        return careTipRepository.findAll(Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt")).stream()
                .map(CareTipResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CareTipResponse> getAdminCareTips(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        return careTipRepository.findAll(pageable).map(CareTipResponse::fromEntity);
    }

    @Override
    public CareTipResponse getCareTipById(Long id) {
        CareTip tip = careTipRepository.findById(id)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("TIP_NOT_FOUND", "Không tìm thấy mẹo chăm sóc!"));
        return CareTipResponse.fromEntity(tip);
    }

    @Override
    public CareTipResponse createCareTip(CareTipRequest request, String email) {

        CareTip newTip = CareTip.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imageUrl(request.getImageUrl())
                .category(request.getCategory())
                .build();

        return CareTipResponse.fromEntity(careTipRepository.save(newTip));
    }

    @Override
    public CareTipResponse updateCareTip(Long id, CareTipRequest request, String email) {

        CareTip tip = careTipRepository.findById(id)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("TIP_NOT_FOUND", "Không tìm thấy mẹo chăm sóc!"));

        if (request.getTitle() != null) tip.setTitle(request.getTitle());
        if (request.getContent() != null) tip.setContent(request.getContent());
        if (request.getImageUrl() != null) tip.setImageUrl(request.getImageUrl());
        if (request.getCategory() != null) tip.setCategory(request.getCategory());

        return CareTipResponse.fromEntity(careTipRepository.save(tip));
    }

    @Override
    public void deleteCareTip(Long id, String email) {
        CareTip tip = careTipRepository.findById(id)
                .orElseThrow(() -> new com.example.plantcare.exception.AppException("TIP_NOT_FOUND", "Không tìm thấy mẹo chăm sóc!"));
        careTipRepository.delete(tip);
    }
}
