package com.team4ever.backend.domain.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscribeResponse {
	@JsonProperty("subscription_combination_id")
	private Integer subscriptionCombinationId;

	@JsonProperty("user_id")
	private Integer userId;

	private Integer price;
}
