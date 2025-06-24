package com.team4ever.backend.domain.maps.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReverseGeocodeRequest {
    private Double latitude;   // 위도
    private Double longitude;  // 경도
}