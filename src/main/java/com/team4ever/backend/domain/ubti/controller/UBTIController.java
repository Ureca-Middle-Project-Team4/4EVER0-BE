package com.team4ever.backend.domain.ubti.controller;

import com.team4ever.backend.domain.ubti.dto.UBTIRequest;
import com.team4ever.backend.domain.ubti.dto.UBTIResult;
import com.team4ever.backend.domain.ubti.service.UBTIService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
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
			- `muneoz`: 친근하고 활발한 MZ 톤
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

		log.info(
				"UBTI 질문 요청 - userId: {}, userPK: {}, session_id: {}, tone: {}",
				userId, userPkId, req.getSession_id(), tone
		);

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
							       "id": 6,
							       "code": "TK-Spicy",
							       "name": "액티브한 매콤타코",
							       "emoji": "🌶",
							       "description": "어디서든 분위기 메이커인 에너자이저 타입이에요. 활발하게 소통하는 걸 좋아하고, 데이터도 빵빵하게 쓰는 걸 선호해요.",
							       "image_url": "https://example.com/images/spicy.png"
							     },
							     "summary": "🍽 오늘의 추천 타코야키 한 접시 나왔습니다!\\n\\n회원님은 소통을 즐기고 실용적인 선택을 중요하게 생각하시는 분이에요. 덕분에 활기차고 매콤한 TK-Spicy 타입이랑 정말 잘 어울리시더라고요!\\n\\n매일매일 연결되는 대화와 데이터 사용이 일상이신 분에게 꼭 맞는 스타일이에요.",
							     "recommendation": {
							       "plans": [
							         {
							           "id": 6,
							           "name": "너겟 34",
							           "description": "데이터 걱정 없이 쓰면서도 통신비는 합리적으로!\\n\\n실속 챙기는 분께 딱이에요 👍"
							         },
							         {
							           "id": 4,
							           "name": "너겟 32",
							           "description": "적당한 데이터와 통화가 포함되어 있어 실용적이에요!\\n\\n일상적인 사용에 적합한 요금제입니다 💡"
							         }
							       ],
							       "subscription": {
							         "id": 1,
							         "name": "유튜브 프리미엄",
							         "description": "광고 없이 콘텐츠를 즐길 수 있는 서비스! 다양한 OTT 혜택으로 더 풍성한 경험을 제공해요 🎥"
							       }
							     },
							     "matching_type": {
							       "id": 7,
							       "code": "TK-SweetChoco",
							       "name": "말 많은 수다타코",
							       "emoji": "🍫",
							       "description": "감정 나누는 걸 소중하게 여기는 따뜻한 커뮤니케이터 타입이에요. 톡도 통화도 자주 하는 분들이 많답니다!",
							       "image_url": "https://example.com/images/sweet.png"
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

		log.info(
				"UBTI 결과 요청 - userId: {}, userPK: {}, session_id: {}, tone: {}",
				userId, userPkId, req.getSession_id(), tone
		);

		return ubtiService.finalResultWrapped(req);
	}

	/**
	 * JWT SecurityContext에서 현재 사용자 ID 추출
	 * @return JWT에서 추출한 사용자 ID (String)
	 * @throws CustomException 인증 정보가 없는 경우
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증되지 않은 사용자의 UBTI API 접근 시도");
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
	private Long getCurrentUserPkId() {
		try {
			String userIdStr = getCurrentUserId();

			User user = userRepository.findByUserId(userIdStr)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없음: {}", userIdStr);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			Long userPkId = user.getId();
			log.debug("JWT userId: {} -> User PK: {}", userIdStr, userPkId);

			return userPkId;

		} catch (CustomException e) {
			throw e; // CustomException은 그대로 던지기
		} catch (Exception e) {
			log.error("사용자 ID 변환 중 예상치 못한 오류 발생: {}", e.getMessage());
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * tone 파라미터 검증
	 * @param tone 입력받은 톤
	 * @return 검증된 톤 (잘못된 경우 기본값 반환)
	 */
	private String validateTone(String tone) {
		if (tone == null || (!tone.equals("general") && !tone.equals("muneoz"))) {
			log.warn("UBTI 잘못된 톤 '{}' - 기본값 'general' 적용", tone);
			return "general";
		}
		log.debug("UBTI 유효한 톤 사용: {}", tone);
		return tone;
	}
}
