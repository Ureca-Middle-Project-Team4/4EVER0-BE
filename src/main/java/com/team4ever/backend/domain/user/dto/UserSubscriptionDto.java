package com.team4ever.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSubscriptionDto {
	private Integer id;

	@JsonProperty("main_title")
	private String mainTitle;

	@JsonProperty("brand_title")
	private String brandTitle;

	private Integer price;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;
}