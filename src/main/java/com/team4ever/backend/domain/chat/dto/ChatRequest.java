package com.team4ever.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
	private String session_id;
	private String message;
	private String tone = "general";  // 기본값: 일반 말투
}