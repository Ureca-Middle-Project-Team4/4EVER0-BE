package com.team4ever.backend.global.response;

import com.team4ever.backend.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse<T> {
	private int status;
	private String message;
	private T data;

	public static <T> BaseResponse<T> success(T data) {
		return new BaseResponse<>(200, "요청 성공", data);
	}

	// ← change here: add <T> on the method signature
	public static <T> BaseResponse<T> error(ErrorCode errorCode) {
		return new BaseResponse<>(errorCode.getStatus().value(), errorCode.getMessage(), null);
	}
}
