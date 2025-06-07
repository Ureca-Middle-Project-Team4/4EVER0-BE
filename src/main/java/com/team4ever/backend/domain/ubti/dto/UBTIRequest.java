package com.team4ever.backend.domain.ubti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UBTIRequest {
	private String session_id;
	private String message;
}
