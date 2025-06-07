package com.team4ever.backend.domain.chat.controller;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@Operation(
			summary = "Chat streaming as plain text",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "각 청크를 문자열로 스트리밍합니다.",
							content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE)
					)
			}
	)

	/**
	 * 채팅 스트리밍 엔드포인트
	 *
	 * 예외사항: 전역 BaseResponse 래퍼를 사용하지 않습니다.
	 *         기본 응답 형식을 위배하지만,
	 *         FastAPI에서 내려오는 NDJSON/plain text 청크를
	 *         Flux<String> 스트리밍으로 유지하기 위한 구현입니다.
	 *
	 * - consumes: application/json
	 * - produces: text/plain (혹은 application/x-ndjson)
	 *
	 * 사용 예시:
	 * Flux<String> chatStream = chatService.getChatResponse(request);
	 * chatStream.subscribe(chunk -> {
	 *     // 각 청크(chunk)마다 처리 로직 작성
	 * });
	 */
	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_PLAIN_VALUE
	)
	public Flux<String> chat(@RequestBody ChatRequest req) {
		return chatService.getChatResponse(req);
	}
}