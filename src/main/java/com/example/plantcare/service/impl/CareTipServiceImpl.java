package com.example.plantcare.service.impl;

import com.example.plantcare.dto.request.CareTipRequest;
import com.example.plantcare.dto.response.CareTipResponse;
import com.example.plantcare.exception.ResourceNotFoundException;
import com.example.plantcare.model.CareTip;
import com.example.plantcare.model.Role;
import com.example.plantcare.model.User;
import com.example.plantcare.repository.CareTipRepository;
import com.example.plantcare.repository.UserRepository;
import com.example.plantcare.service.CareTipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class CareTipServiceImpl implements CareTipService {

    private final CareTipRepository careTipRepository;
    private final UserRepository userRepository;

    private User getAdminByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        if (user.getRole() != Role.ADMIN) {
            throw new com.example.plantcare.exception.AppException("FORBIDDEN_ADMIN_ONLY", "Chỉ ADMIN mới có quyền thực hiện hành động này!");
        }
        return user;
    }

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
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẹo!"));
        return CareTipResponse.fromEntity(tip);
    }

    @Override
    public CareTipResponse createCareTip(CareTipRequest request, String email) {
        getAdminByEmail(email); // Validate Admin

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
        getAdminByEmail(email); // Validate Admin

        CareTip tip = careTipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẹo!"));

        if (request.getTitle() != null) tip.setTitle(request.getTitle());
        if (request.getContent() != null) tip.setContent(request.getContent());
        if (request.getImageUrl() != null) tip.setImageUrl(request.getImageUrl());
        if (request.getCategory() != null) tip.setCategory(request.getCategory());

        return CareTipResponse.fromEntity(careTipRepository.save(tip));
    }

    @Override
    public void deleteCareTip(Long id, String email) {
        getAdminByEmail(email); // Validate Admin
        CareTip tip = careTipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẹo!"));
        careTipRepository.delete(tip);
    }
}

