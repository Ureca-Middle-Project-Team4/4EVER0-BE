package com.team4ever.backend.domain.recommend.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class MultiTurnRequest {
	private String session_id;
	private String age_group;
	private List<String> interests;
	private List<String> time_usage;
}
