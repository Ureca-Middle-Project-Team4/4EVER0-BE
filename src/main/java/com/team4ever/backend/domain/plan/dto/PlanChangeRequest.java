package com.team4ever.backend.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class PlanChangeRequest {
	@NotNull(message = "요금제 ID는 필수입니다.")
	@Positive(message = "요금제 ID는 양수여야 합니다.")
	@JsonProperty("plan_id")
	private Integer planId;
}