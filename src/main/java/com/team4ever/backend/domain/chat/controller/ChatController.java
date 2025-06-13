package com.team4ever.backend.domain.chat.controller;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "채팅 API", description = "AI 채팅 및 추천 서비스")
public class ChatController {

	private final ChatService chatService;

	// 유효한 톤 목록을 상수로 관리
	private static final Set<String> VALID_TONES = Set.of("general", "muneoz");
	private static final String DEFAULT_TONE = "general";

	@Operation(
			summary = "채팅 대화",
			description = "사용자와 AI 간의 대화를 스트리밍으로 처리합니다.\n\n" +
					"**tone 파라미터:**\n" +
					"- `general`: 정중하고 전문적인 상담원 톤\n" +
					"- `muneoz`: 친근하고 활발한 무너즈 캐릭터 톤"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "채팅 응답 스트림 성공",
					content = @Content(mediaType = "text/event-stream")
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청"
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류"
			)
	})
	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> chat(
			@Parameter(
					description = "채팅 요청 정보",
					required = true,
					content = @Content(schema = @Schema(implementation = ChatRequest.class))
			)
			@Valid @RequestBody ChatRequest request
	) {
		// tone 검증 및 정규화
		String validatedTone = validateAndNormalizeTone(request.getTone());
		request.setTone(validatedTone);

		log.info("채팅 요청 - session_id: {}, tone: {}, message: {}",
				request.getSession_id(), validatedTone,
				request.getMessage().length() > 50 ?
						request.getMessage().substring(0, 50) + "..." :
						request.getMessage());

		return chatService.streamChat(request);
	}

	@Operation(
			summary = "좋아요 기반 추천",
			description = "사용자가 좋아요한 브랜드를 기반으로 구독 서비스를 추천합니다.\n\n" +
					"**tone 파라미터:**\n" +
					"- `general`: 정중하고 전문적인 상담원 톤\n" +
					"- `muneoz`: 친근하고 활발한 무너즈 캐릭터 톤"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "추천 응답 스트림 성공",
					content = @Content(mediaType = "text/event-stream")
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청"
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류"
			)
	})
	@PostMapping(
			value = "/likes",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> chatLikes(
			@Parameter(
					description = "좋아요 기반 추천 요청 정보",
					required = true,
					content = @Content(schema = @Schema(implementation = ChatRequest.class))
			)
			@Valid @RequestBody ChatRequest request  // @Valid 추가
	) {
		String validatedTone = validateAndNormalizeTone(request.getTone());
		request.setTone(validatedTone);

		log.info("좋아요 기반 추천 요청 - session_id: {}, tone: {}",
				request.getSession_id(), validatedTone);

		return chatService.streamChatLikes(request);
	}

	/**
	 * tone 파라미터 검증 및 정규화
	 * @param tone 입력받은 톤
	 * @return 검증된 톤 (잘못된 경우 기본값 반환)
	 */
	private String validateAndNormalizeTone(String tone) {
		if (tone == null || tone.trim().isEmpty()) {
			log.info("톤이 null/empty - 기본값 '{}' 적용", DEFAULT_TONE);
			return DEFAULT_TONE;
		}

		String normalizedTone = tone.trim().toLowerCase();
		if (VALID_TONES.contains(normalizedTone)) {
			log.debug("유효한 톤 사용: {}", normalizedTone);
			return normalizedTone;
		} else {
			log.warn("잘못된 톤 '{}' - 기본값 '{}' 적용", tone, DEFAULT_TONE);
			return DEFAULT_TONE;
		}
	}
}