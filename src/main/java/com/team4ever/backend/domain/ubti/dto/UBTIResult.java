package com.team4ever.backend.domain.ubti.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  // 알려지지 않은 필드 무시
public class UBTIResult {

	@JsonProperty("ubti_type")
	private UBTIType ubti_type;

	@JsonProperty("summary")
	private String summary;

	@JsonProperty("recommendation")
	private Recommendation recommendation;

	@JsonProperty("matching_type")
	private UBTIType matching_type;

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class UBTIType {
		@JsonProperty("code")
		private String code;

		@JsonProperty("name")
		private String name;

		@JsonProperty("emoji")
		private String emoji;

		@JsonProperty("description")
		private String description;
	}

	@Getter
	@Setter
	@ToString
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Recommendation {
		@JsonProperty("plans")
		private List<Plan> plans;

		@JsonProperty("subscription")
		private Subscription subscription;

		@Getter
		@Setter
		@ToString
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Plan {
			@JsonProperty("name")
			private String name;

			@JsonProperty("description")
			private String description;
		}

		@Getter
		@Setter
		@ToString
		@JsonIgnoreProperties(ignoreUnknown = true)
		public static class Subscription {
			@JsonProperty("name")
			private String name;

			@JsonProperty("description")
			private String description;
		}
	}
}