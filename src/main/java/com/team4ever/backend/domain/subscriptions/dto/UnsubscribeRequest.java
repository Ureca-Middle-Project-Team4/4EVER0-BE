package com.team4ever.backend.domain.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnsubscribeRequest {
	@JsonProperty("subscription_combination_id")
	private Integer subscriptionCombinationId;
}