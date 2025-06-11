package com.team4ever.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

   // 출석체크 전용 에러
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	ALREADY_CHECKED(HttpStatus.CONFLICT, "이미 오늘 출석했습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	INVALID_USER_ID(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 ID입니다."),

	// 구독 관련 에러
	SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 구독 상품을 찾을 수 없습니다."),
	BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 브랜드를 찾을 수 없습니다."),
	SUBSCRIPTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 구독입니다."),
	INVALID_SUBSCRIPTION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 구독 요청입니다."),

	// --- 쿠폰 모듈 전용 ---
	COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
	COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "유효 기간이 아닙니다."),
	COUPON_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 발급된 쿠폰입니다."),
	COUPON_NOT_CLAIMED(HttpStatus.NOT_FOUND, "발급된 쿠폰이 없습니다."),
	COUPON_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 쿠폰입니다."),

	// 팝업스토어 전용 에러
	POPUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 팝업스토어가 없습니다."),
	POPUP_ACCESS_DENIED(HttpStatus.FORBIDDEN, "팝업스토어 조회 권한이 없습니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
