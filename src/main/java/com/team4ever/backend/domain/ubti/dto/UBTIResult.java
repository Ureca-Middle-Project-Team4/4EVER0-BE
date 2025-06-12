package com.team4ever.backend.domain.ubti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UBTIResult {

	private UBTIType ubti_type;
	private String summary;
	private Recommendation recommendation;
	private UBTIType matching_type;

	@Getter @Setter
	public static class UBTIType {
		private String code;
		private String name;
		private String emoji;
		private String description;
	}

	@Getter @Setter
	public static class Recommendation {
		private Plan plan;
		private Subscription subscription;

		@Getter @Setter
		public static class Plan {
			private String name;
			private String description;
		}

		@Getter @Setter
		public static class Subscription {
			private String name;
			private String description;
		}
	}
}
