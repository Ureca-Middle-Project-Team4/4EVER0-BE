package com.team4ever.backend.domain.plan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanResponse {
	private Integer id;
	private String name;
	private String price;
	private String description;
	private String data;
	private String speed;
	private String sms;
	private String voice;

	@JsonProperty("share_data")
	private String shareData;
}