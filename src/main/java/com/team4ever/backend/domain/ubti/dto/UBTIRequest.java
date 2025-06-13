package com.team4ever.backend.domain.ubti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UBTIRequest {
	private String session_id;
	private String message;
	private String tone = "general";  // 기본값: 일반 말투
}