package com.team4ever.backend.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter @Setter
public class ChatRequest {
	private String session_id;
	private String message;
	private Optional<String> persona = Optional.of("default");
}
