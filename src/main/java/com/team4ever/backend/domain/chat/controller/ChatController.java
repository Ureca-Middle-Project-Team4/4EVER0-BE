package com.team4ever.backend.domain.chat.controller;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@Operation(
			summary = "채팅 대화",
			description = "사용자와 AI 간의 대화를 스트리밍으로 처리합니다. tone 파라미터로 말투를 선택할 수 있습니다."
	)
	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> chat(
			@RequestBody ChatRequest request
	) {
		// tone 검증 및 로깅
		String tone = validateTone(request.getTone());
		request.setTone(tone);

		log.info("채팅 요청 - session_id: {}, tone: {}", request.getSession_id(), tone);

		return chatService.streamChat(request);
	}

	@Operation(
			summary = "좋아요 기반 추천",
			description = "사용자가 좋아요한 브랜드를 기반으로 구독 서비스를 추천합니다."
	)
	@PostMapping(
			value = "/likes",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> chatLikes(
			@RequestBody ChatRequest request  // tone 포함
	) {
		String tone = validateTone(request.getTone());
		request.setTone(tone);

		log.info("좋아요 기반 추천 요청 - session_id: {}, tone: {}", request.getSession_id(), tone);

		return chatService.streamChatLikes(request);
	}

	/**
	 * tone 파라미터 검증
	 */
	private String validateTone(String tone) {
		if (tone == null || (!tone.equals("general") && !tone.equals("muneoz"))) {
			log.warn("Invalid tone: {}, defaulting to 'general'", tone);
			return "general";
		}
		return tone;
	}
}