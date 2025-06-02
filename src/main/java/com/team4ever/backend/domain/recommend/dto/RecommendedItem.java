package com.team4ever.backend.domain.recommend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RecommendedItem {
	private String title;
	private String description;
	private String image_url;
	private String detail_url;
	private String price;
	private String reason;
}
