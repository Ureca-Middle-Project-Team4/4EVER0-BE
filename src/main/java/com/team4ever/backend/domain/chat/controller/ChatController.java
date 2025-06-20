package com.team4ever.backend.domain.chat.controller;

import com.team4ever.backend.domain.chat.dto.ChatRequest;
import com.team4ever.backend.domain.chat.dto.UsageRecommendRequest;
import com.team4ever.backend.domain.chat.service.ChatService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private final UserRepository userRepository;

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

	@Operation(
			summary = "사용량 기반 요금제 추천",
			description = "사용자의 실제 데이터/음성/SMS 사용 패턴을 AI로 분석하여 최적의 요금제를 추천합니다.\n\n" +
					"**특징:**\n" +
					"- 6가지 사용자 타입 분석 (헤비/안정추구/균형/스마트/절약/라이트)\n" +
					"- 구체적 절약/추가 비용 계산 (월/연간)\n" +
					"- 실생활 비교 설명 (치킨값, 넷플릭스 등)\n" +
					"- 가중평균 사용률 계산 (데이터 60% + 음성 30% + SMS 10%)\n\n" +
					"**tone 파라미터:**\n" +
					"- `general`: 정중하고 전문적인 상담원 톤\n" +
					"- `muneoz`: 친근하고 활발한 무너즈 캐릭터 톤\n\n" +
					"**응답 형식:**\n" +
					"1. usage_analysis - 사용량 분석 데이터\n" +
					"2. plan_recommendations - 추천 요금제 카드\n" +
					"3. message_start - 스트리밍 시작\n" +
					"4. message_chunk - 맞춤 설명 (스트리밍)\n" +
					"5. message_end - 스트리밍 완료"
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "사용량 기반 추천 응답 스트림 성공",
					content = @Content(mediaType = "text/event-stream")
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청"
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증 실패 (JWT 토큰 필요)"
			),
			@ApiResponse(
					responseCode = "404",
					description = "사용자 정보 없음"
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류"
			)
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping(
			value = "/usage",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> usageRecommend(
			@Parameter(
					description = "사용량 기반 추천 요청 정보",
					required = true,
					content = @Content(schema = @Schema(implementation = UsageRecommendRequest.class))
			)
			@Valid @RequestBody UsageRecommendRequest request
	) {
		// tone 검증 및 정규화
		String validatedTone = validateAndNormalizeTone(request.getTone());
		request.setTone(validatedTone);

		// JWT에서 사용자 ID 추출
		int userId = getCurrentUserIdAsInt();

		log.info("사용량 기반 추천 요청 - user_id: {}, tone: {}", userId, validatedTone);

		return chatService.streamUsageRecommend(userId, request);
	}

	/**
	 * JWT SecurityContext에서 현재 사용자 ID 추출
	 * @return JWT에서 추출한 사용자 ID (String)
	 * @throws CustomException 인증 정보가 없는 경우
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증되지 않은 사용자의 사용량 추천 API 접근 시도");
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String userId = (String) authentication.getPrincipal();
		log.debug("JWT에서 추출한 사용자 ID: {}", userId);
		return userId;
	}

	/**
	 * JWT에서 추출한 User.userId(String)로 User 엔티티를 조회하여 PK(Long) 반환
	 * @return User 엔티티의 PK (Long)
	 * @throws CustomException 사용자를 찾을 수 없는 경우
	 */
	private Long getCurrentUserPk() {
		try {
			String userIdStr = getCurrentUserId();

			// User.userId(String)로 User 엔티티 조회
			User user = userRepository.findByUserId(userIdStr)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없음: {}", userIdStr);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			Long userPkId = user.getId();
			log.debug("JWT userId: {} -> User PK: {}", userIdStr, userPkId);

			return userPkId;

		} catch (CustomException e) {
			throw e; // CustomException은 그대로 재던지기
		} catch (Exception e) {
			log.error("사용자 ID 변환 중 예상치 못한 오류 발생: {}", e.getMessage());
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * FastAPI에서 요구하는 int 타입 사용자 ID 반환
	 * @return 사용자 PK를 int로 변환
	 * @throws CustomException 변환 실패 시
	 */
	private int getCurrentUserIdAsInt() {
		try {
			Long userPk = getCurrentUserPk();

			// Long에서 int로 안전하게 변환
			if (userPk > Integer.MAX_VALUE) {
				log.error("사용자 PK가 int 범위를 초과: {}", userPk);
				throw new CustomException(ErrorCode.INVALID_USER_ID);
			}

			return userPk.intValue();
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("사용자 ID int 변환 중 오류: {}", e.getMessage());
			throw new CustomException(ErrorCode.INVALID_USER_ID);
		}
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