package com.team4ever.backend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class ChatRequest {
	private String session_id;
	private String message;

	@Pattern(regexp = "^(general|muneoz)$", message = "톤은 'general' 또는 'muneoz'만 가능합니다.")
	private String tone = "general";  // 기본값: general

	// 톤 정규화 메서드
	public void setTone(String tone) {
		if (tone == null || tone.trim().isEmpty()) {
			this.tone = "general";
		} else if ("general".equals(tone) || "muneoz".equals(tone)) {
			this.tone = tone;
		} else {
			this.tone = "general";  // 잘못된 값이면 기본값으로
		}
	}
}