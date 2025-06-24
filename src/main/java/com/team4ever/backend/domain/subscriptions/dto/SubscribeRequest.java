package com.team4ever.backend.domain.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubscribeRequest {
	@JsonProperty("subscription_id")
	private Integer subscriptionId;

	@JsonProperty("brand_id")
	private Integer brandId;
}
