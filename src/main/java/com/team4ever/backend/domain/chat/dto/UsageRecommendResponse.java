package com.team4ever.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsageRecommendResponse {
	private String type;
	private Object data;

	// 사용량 분석 데이터 전용 내부 클래스
	@Data
	public static class UsageAnalysisData {
		@JsonProperty("user_id")
		private int userId;

		@JsonProperty("current_plan")
		private String currentPlan;

		@JsonProperty("current_price")
		private int currentPrice;

		@JsonProperty("remaining_data")
		private int remainingData;

		@JsonProperty("remaining_voice")
		private int remainingVoice;

		@JsonProperty("remaining_sms")
		private int remainingSms;

		@JsonProperty("usage_percentage")
		private double usagePercentage;
	}

	// 요금제 추천 데이터 전용 내부 클래스
	@Data
	public static class PlanRecommendationsData {
		private List<PlanInfo> plans;
	}

	// 추천 요금제 정보
	@Data
	public static class PlanInfo {
		private int id;
		private String name;
		private int price;
		private String data;
		private String voice;
		private String speed;

		@JsonProperty("share_data")
		private String shareData;

		private String sms;
		private String description;
	}

	// 메시지 청크 데이터 (스트리밍)
	@Data
	public static class MessageChunkData {
		private String content;
	}
}