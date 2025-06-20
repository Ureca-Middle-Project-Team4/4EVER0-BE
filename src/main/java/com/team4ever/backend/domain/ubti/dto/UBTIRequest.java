package com.team4ever.backend.domain.ubti.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class UBTIRequest {
	private String session_id;
	private String message;

	@Pattern(regexp = "^(general|muneoz)$", message = "톤은 'general' 또는 'muneoz'만 가능합니다.")
	private String tone = "general";

	public void setTone(String tone) {
		if (tone == null || tone.trim().isEmpty()) {
			this.tone = "general";
		} else if ("general".equals(tone) || "muneoz".equals(tone)) {
			this.tone = tone;
		} else {
			this.tone = "general";
		}
	}
}