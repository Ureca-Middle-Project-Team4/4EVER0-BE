package com.team4ever.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

	// 팝업스토어 전용 에러
	POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 팝업스토어가 없습니다."),
	POPUP_ACCESS_DENIED(HttpStatus.FORBIDDEN, "팝업스토어 조회 권한이 없습니다."),

	//미션 전용 에러
	MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 미션이 없습니다."),
	MISSION_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "아직 완료되지 않은 미션입니다."),
	REWARD_ALREADY_RECEIVED(HttpStatus.BAD_REQUEST, "이미 보상을 수령한 미션입니다."),
	USER_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 미션 정보를 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
