package com.team4ever.backend.domain.naver.controller;

import com.team4ever.backend.domain.naver.dto.ReverseGeocodeRequest;
import com.team4ever.backend.domain.naver.dto.ReverseGeocodeResponse;
import com.team4ever.backend.domain.naver.service.ReverseGeocodingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geocoding")
@RequiredArgsConstructor
public class ReverseGeocodingController {
    private final ReverseGeocodingService reverseGeocodingService;

    @PostMapping("/reverse")
    public ResponseEntity<ReverseGeocodeResponse> reverseGeocode(@RequestBody ReverseGeocodeRequest request) {
        ReverseGeocodeResponse result = reverseGeocodingService.findAddressByCoordinates(
                request.getLatitude(), request.getLongitude()
        );
        return ResponseEntity.ok(result);
    }
}
