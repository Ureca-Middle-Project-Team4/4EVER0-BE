package com.team4ever.backend.domain.popups.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@Getter
@Setter
public class NearbyPopupsRequest {

	@NotNull(message = "위도는 필수입니다.")
	@DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
	@DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
	private Double lat;

	@NotNull(message = "경도는 필수입니다.")
	@DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
	@DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
	private Double lng;

	// 검색 반경 (기본값: 5km)
	private Double radius = 5.0;
}