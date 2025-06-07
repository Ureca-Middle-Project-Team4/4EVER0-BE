package com.team4ever.backend.domain.ubti.controller;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.domain.ubti.service.UBTIService;
import com.team4ever.backend.global.response.BaseResponse;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ubti")
@RequiredArgsConstructor
public class UBTIController {

	private final UBTIService ubtiService;

	@PostMapping("/question")
	public Mono<BaseResponse<Object>> nextQuestion(@RequestBody UBTIRequest req) {
		return ubtiService.nextQuestion(req)
				.map(BaseResponse::success)
				.onErrorResume(ex ->
						Mono.just(BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR))
				);
	}

	@PostMapping("/result")
	public Mono<BaseResponse<UBTIResult>> finalResult(@RequestBody UBTIRequest req) {
		return ubtiService.finalResult(req)
				.map(BaseResponse::success)
				.onErrorResume(ex ->
						Mono.just(BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR))
				);
	}

}
