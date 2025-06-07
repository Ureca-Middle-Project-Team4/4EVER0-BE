package com.team4ever.backend.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatResponse {
	private String session_id;
	private String message;
}
