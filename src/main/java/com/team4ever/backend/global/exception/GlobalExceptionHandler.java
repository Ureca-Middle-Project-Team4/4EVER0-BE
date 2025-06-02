package com.team4ever.backend.global.exception;

import com.team4ever.backend.global.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public BaseResponse<?> handleCustomException(CustomException e) {
		return BaseResponse.error(e.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	public BaseResponse<?> handleException(Exception e) {
		return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR);
	}
}
