package com.team4ever.backend.domain.ubti.controller;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.domain.ubti.service.UBTIService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/ubti")
@RequiredArgsConstructor
@Tag(name = "UBTI API", description = "UBTI 타코시그널 테스트 API (인증 필요)")
@SecurityRequirement(name = "cookieAuth")
public class UBTIController {

	private final UBTIService ubtiService;
	private final UserRepository userRepository;

	@Operation(
			summary = "UBTI 질문 스트리밍",
			description = """
            UBTI 질문을 실시간으로 스트리밍하여 전송합니다.
            
            **인증 필요:** 로그인한 사용자만 이용 가능
            
            **tone 파라미터:**
            - `general`: 정중하고 전문적인 톤
            - `muneoz`: 친근하고 활발한 무너즈 캐릭터 톤
            """
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "질문 스트리밍 성공"),
			@ApiResponse(responseCode = "401", description = "인증 필요"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청"),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping(
			value = "/question",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.TEXT_EVENT_STREAM_VALUE
	)
	public Flux<ServerSentEvent<String>> nextQuestion(@RequestBody UBTIRequest req) {
		log.info("=== UBTI 질문 요청 시작 ===");

		// 인증된 사용자 정보 추가
		String userId = getCurrentUserId();
		Long userPkId = getCurrentUserPkId();

		// tone 검증
		String tone = validateTone(req.getTone());
		req.setTone(tone);

		log.info("UBTI 질문 요청 - userId: {}, userPK: {}, session_id: {}, tone: {}",
				userId, userPkId, req.getSession_id(), tone);

		return ubtiService.nextQuestionStream(req);
	}

	@Operation(
			summary = "UBTI 검사 최종 결과 조회",
			description = """
            모든 질문 완료 후 UBTI 타입과 요금제/구독 서비스 추천 결과를 반환합니다.
            
            **인증 필요:** 로그인한 사용자만 이용 가능
            """
	)
	@ApiResponses({
			@ApiResponse(
					responseCode = "200",
					description = "검사 결과 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = BaseResponse.class),
							examples = {
									@ExampleObject(
											name = "일반 말투 결과",
											description = "정중한 말투로 응답",
											value = """
                            {
                              "status": 200,
                              "message": "요청 성공",
                              "data": {
                                "ubti_type": {
                                  "description": "편안한 소통을 좋아하는 타입입니다."
                                },
                                "summary": "고객님의 통신 성향을 분석한 결과입니다.",
                                "recommendations": {
                                  "plans": ["프리미엄 요금제"],
                                  "subscriptions": ["넷플릭스", "유튜브 프리미엄"]
                                }
                              }
                            }
                            """
									),
									@ExampleObject(
											name = "무너즈 말투 결과",
											description = "친근한 말투로 응답",
											value = """
                            {
                              "status": 200,
                              "message": "요청 성공",
                              "data": {
                                "ubti_type": {
                                  "description": "편안한 소통 완전 좋아하는 타입이야! 💜"
                                },
                                "summary": "네 답변 보니까 완전 이런 스타일이네! 🔥",
                                "recommendations": {
                                  "plans": ["무제한 요금제 추천해!"],
                                  "subscriptions": ["넷플릭스 꼭 써봐!", "유튜브 프리미엄 최고야!"]
                                }
                              }
                            }
                            """
									)
							}
					)
			),
			@ApiResponse(responseCode = "401", description = "인증 필요"),
			@ApiResponse(responseCode = "400", description = "잘못된 요청"),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping(
			value = "/result",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public Mono<BaseResponse<UBTIResult>> finalResult(@RequestBody UBTIRequest req) {
		log.info("=== UBTI 결과 요청 시작 ===");

		// 인증된 사용자 정보 추가
		String userId = getCurrentUserId();
		Long userPkId = getCurrentUserPkId();

		// tone 검증
		String tone = validateTone(req.getTone());
		req.setTone(tone);

		log.info("UBTI 결과 요청 - userId: {}, userPK: {}, session_id: {}, tone: {}",
				userId, userPkId, req.getSession_id(), tone);

		return ubtiService.finalResultWrapped(req);
	}

	/**
	 * 현재 인증된 사용자 ID 추출 (필수)
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증되지 않은 사용자의 UBTI API 접근 시도");
			throw new RuntimeException("로그인이 필요한 서비스입니다.");
		}

		String userId = (String) authentication.getPrincipal();
		log.debug("현재 사용자 ID: {}", userId);
		return userId;
	}

	/**
	 * 현재 사용자의 PK 추출
	 */
	private Long getCurrentUserPkId() {
		try {
			String userIdStr = getCurrentUserId();

			User user = userRepository.findByUserId(userIdStr)
					.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIdStr));

			Long userPkId = user.getId();
			log.debug("JWT userId: {} -> User PK: {}", userIdStr, userPkId);

			return userPkId;

		} catch (Exception e) {
			log.error("사용자 ID 변환 중 오류 발생: {}", e.getMessage());
			throw new RuntimeException("사용자 정보를 조회할 수 없습니다.");
		}
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