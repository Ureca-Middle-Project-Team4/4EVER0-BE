package com.team4ever.backend.domain.ubti.controller;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.domain.ubti.service.UBTIService;
import com.team4ever.backend.global.response.BaseResponse;
import com.team4ever.backend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ubti")
@RequiredArgsConstructor
public class UBTIController {

	private final UBTIService ubtiService;

	@Operation(summary = "통신유형 검사")
	@PostMapping(
			value = "/question",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE // 스트리밍 명시
	)
	public Flux<ServerSentEvent<String>> nextQuestion(@RequestBody UBTIRequest req) {
		return ubtiService.nextQuestionStream(req);
	}

	@Operation(summary = "해당 세션의 검사결과 조회")
	@PostMapping("/result")
	public Mono<BaseResponse<UBTIResult>> finalResult(@RequestBody UBTIRequest req) {
		return ubtiService.finalResultWrapped(req);
	}
}