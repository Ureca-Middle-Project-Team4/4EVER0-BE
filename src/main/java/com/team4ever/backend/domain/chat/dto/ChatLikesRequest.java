package com.team4ever.backend.domain.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLikesRequest {
	private String session_id;
	private String message;
}
