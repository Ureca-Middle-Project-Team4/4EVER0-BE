package com.team4ever.backend.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanChangeResponse {
	@JsonProperty("plan_id")
	private Integer planId;

	@JsonProperty("plan_name")
	private String planName;

	@JsonProperty("changed_at")
	private LocalDateTime changedAt;

	private String message;
}