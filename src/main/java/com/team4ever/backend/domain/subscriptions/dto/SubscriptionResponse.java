package com.team4ever.backend.domain.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionResponse {
	private Integer id;
	private String title;

	@JsonProperty("image_url")
	private String imageUrl;

	private String category;
	private Integer price;
}