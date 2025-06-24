package com.team4ever.backend.domain.maps.service;

import com.team4ever.backend.domain.maps.dto.ReverseGeocodeResponse;
import com.team4ever.backend.domain.maps.repository.ReverseGeocodingRepository;
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

    // 시-구-동만 추출
    public String getSimpleAddress(ReverseGeocodeResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            return "주소 정보 없음";
        }
        ReverseGeocodeResponse.Result result = response.getResults().get(0);
        if (result.getRegion() == null) return "주소 정보 없음";

        ReverseGeocodeResponse.Result.Region region = result.getRegion();
        String area1 = (region.getArea1() != null && region.getArea1().getName() != null) ? region.getArea1().getName() : "";
        String area2 = (region.getArea2() != null && region.getArea2().getName() != null) ? region.getArea2().getName() : "";
        String area3 = (region.getArea3() != null && region.getArea3().getName() != null) ? region.getArea3().getName() : "";

        return String.format("%s %s %s", area1, area2, area3).trim();
    }
}