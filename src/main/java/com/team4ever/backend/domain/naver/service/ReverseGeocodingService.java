package com.team4ever.backend.domain.naver.service;

import com.team4ever.backend.domain.naver.dto.ReverseGeocodeResponse;
import com.team4ever.backend.domain.naver.repository.ReverseGeocodingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReverseGeocodingService {
    private final ReverseGeocodingRepository reverseGeocodingRepository;

    public ReverseGeocodeResponse findAddressByCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null) throw new IllegalArgumentException("좌표 필수");
        return reverseGeocodingRepository.reverseGeocode(lat, lng);
    }
}