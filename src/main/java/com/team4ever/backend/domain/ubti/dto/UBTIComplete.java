package com.team4ever.backend.domain.ubti.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UBTIComplete {
	private boolean completed = true;
	private String message = "모든 질문이 완료되었습니다.";
}
