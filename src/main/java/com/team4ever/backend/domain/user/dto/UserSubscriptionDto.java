package com.team4ever.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSubscriptionDto {
	private Integer id;  // user_subscription_combinations.id (PK)

	@JsonProperty("subscription_combination_id")  // 해지할 때 사용할 값
	private Integer subscriptionCombinationId;

	@JsonProperty("main_title")
	private String mainTitle;

	@JsonProperty("brand_title")
	private String brandTitle;

	private Integer price;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	// @Query에서 사용할 생성자 추가
	public UserSubscriptionDto(Integer id, Integer subscriptionCombinationId, String mainTitle,
							   String brandTitle, Integer price, LocalDateTime createdAt) {
		this.id = id;
		this.subscriptionCombinationId = subscriptionCombinationId;
		this.mainTitle = mainTitle;
		this.brandTitle = brandTitle;
		this.price = price;
		this.createdAt = createdAt;
	}
}