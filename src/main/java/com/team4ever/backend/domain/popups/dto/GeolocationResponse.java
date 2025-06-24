package com.team4ever.backend.domain.popups.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeolocationResponse {

	@JsonProperty("returnCode")
	private String returnCode;

	@JsonProperty("requestId")
	private String requestId;

	@JsonProperty("geoLocation")
	private GeoLocationData geoLocation;

	@Getter
	@Setter
	public static class GeoLocationData {
		@JsonProperty("country")
		private String country;

		@JsonProperty("code")
		private String code;

		@JsonProperty("r1")
		private String r1; // 시/도

		@JsonProperty("r2")
		private String r2; // 시/구/군

		@JsonProperty("r3")
		private String r3; // 동/면/읍

		@JsonProperty("lat")
		private Double lat; // 위도

		@JsonProperty("long")
		private Double longitude; // 경도

		@JsonProperty("net")
		private String net; // 통신사

		public Double getLng() {
			return longitude;
		}
	}
}