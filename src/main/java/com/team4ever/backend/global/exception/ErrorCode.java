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

	// 요금제 관련 에러
	PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "요금제를 찾을 수 없습니다."),
	PLAN_ALREADY_USING(HttpStatus.CONFLICT, "이미 사용 중인 요금제입니다."),
	PLAN_CHANGE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "요금제 변경 중 오류가 발생했습니다."),
	PLAN_CANCEL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "요금제 해지 중 오류가 발생했습니다."),
	INVALID_PLAN_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요금제 요청입니다."),

	// 구독 관련 에러
	SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 구독 상품을 찾을 수 없습니다."),
	BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID의 브랜드를 찾을 수 없습니다."),
	SUBSCRIPTION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 구독입니다."),
	INVALID_SUBSCRIPTION_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 구독 요청입니다."),
	SUBSCRIPTION_NOT_SUBSCRIBED(HttpStatus.BAD_REQUEST, "구독하지 않은 상품입니다."),
	SUBSCRIPTION_CANCELLATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "구독 해지 처리 중 오류가 발생했습니다."),

	// --- 쿠폰 모듈 전용 ---
	COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
	COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "유효 기간이 아닙니다."),
	COUPON_ALREADY_CLAIMED(HttpStatus.CONFLICT, "이미 발급된 쿠폰입니다."),
	COUPON_NOT_CLAIMED(HttpStatus.NOT_FOUND, "발급된 쿠폰이 없습니다."),
	COUPON_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용된 쿠폰입니다."),

	// 채팅 및 추천 관련 에러
	CHAT_AI_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI 채팅 서비스 연결 오류가 발생했습니다."),
	USAGE_RECOMMENDATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "사용량 기반 추천 생성 중 오류가 발생했습니다."),
	LIKES_RECOMMENDATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "좋아요 기반 추천 생성 중 오류가 발생했습니다."),
	CHAT_STREAMING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "채팅 스트리밍 중 오류가 발생했습니다."),
	INVALID_TONE_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 톤 요청입니다."),
	FASTAPI_CONNECTION_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "FastAPI 서버 연결 오류가 발생했습니다."),
	USER_USAGE_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 사용량 데이터를 찾을 수 없습니다."),

	// UBTI 관련 에러
	UBTI_DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "UBTI 타입 데이터를 찾을 수 없습니다."),
	UBTI_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "UBTI 결과 생성 중 오류가 발생했습니다."),
	UBTI_INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 UBTI 요청입니다."),
	UBTI_AI_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스 연결 오류가 발생했습니다."),
	UBTI_JSON_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UBTI 결과 파싱 중 오류가 발생했습니다."),
	UBTI_PROMPT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UBTI 프롬프트 생성 중 오류가 발생했습니다."),
	UBTI_DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UBTI 데이터베이스 연결 오류가 발생했습니다."),
	// 위치 기반 API 에러
	GEOLOCATION_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "위치 정보 조회 중 오류가 발생했습니다."),

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
