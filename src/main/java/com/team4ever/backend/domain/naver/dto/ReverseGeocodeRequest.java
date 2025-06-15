package com.team4ever.backend.domain.naver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReverseGeocodeRequest {
    private Double latitude;   // 위도
    private Double longitude;  // 경도
}