package com.team4ever.backend.domain.popups.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLocationResponse {
	private Double latitude;
	private Double longitude;
	private String address;

	@JsonProperty("nearby_popups")
	private java.util.List<NearbyPopupResponse> nearbyPopups;
}