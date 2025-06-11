package com.team4ever.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatRequest {
	@JsonProperty("session_id")
	private String sessionId;

	@JsonProperty("message")
	private String message;
}

