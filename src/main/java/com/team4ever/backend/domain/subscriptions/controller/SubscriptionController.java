package com.team4ever.backend.domain.subscriptions.controller;

import com.team4ever.backend.domain.subscriptions.dto.*;
import com.team4ever.backend.domain.subscriptions.service.SubscriptionService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static com.team4ever.backend.global.response.BaseResponse.*;

@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "📺 구독 API", description = "구독 상품 조회, 가입, 해지 관련 API")
public class SubscriptionController {

	private final SubscriptionService subscriptionService;
	private final UserRepository userRepository;

	@Operation(
			summary = "메인 구독 상품 전체 조회",
			description = """
            메인 페이지에 표시되는 구독 상품 목록을 조회합니다.
            
            **특징:**
            - 인증 불필요 (공개 API)
            - 추천 구독 상품 위주로 구성
            - 인기순으로 정렬하여 제공
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "메인 구독 상품 조회 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "메인 구독 상품 조회 성공",
                          "data": [
                            {
                              "id": 1,
                              "title": "넷플릭스 프리미엄",
                              "image_url": "https://example.com/netflix.png",
                              "category": "ENTERTAINMENT",
                              "price": 17000
                            }
                          ]
                        }
                        """
							)
					)
			)
	})
	@GetMapping("/main")
	public ResponseEntity<BaseResponse<List<SubscriptionResponse>>> getMainSubscriptions() {
		log.info("메인 구독 상품 전체 조회 요청");

		List<SubscriptionResponse> subscriptions = subscriptionService.getMainSubscriptions();

		log.info("메인 구독 상품 조회 완료 - 상품 수: {}", subscriptions.size());
		return ResponseEntity.ok(success(subscriptions));
	}

	@Operation(
			summary = "라이프 구독 브랜드 조회 (전체 or 카테고리별)",
			description = """
            라이프스타일 관련 구독 브랜드를 조회합니다.
            
            **조회 옵션:**
            - category 없음: 전체 브랜드 조회
            - category 지정: 특정 카테고리의 브랜드만 조회
            
            **지원 카테고리:**
            - 도서/콘텐츠
            - 디저트/음료
            - 편의점/쇼핑
            - 카페/음료
            - 베이커리
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "브랜드 조회 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "브랜드 조회 성공",
                          "data": [
                            {
                              "id": 1,
                              "title": "베스킨라빈스",
                              "image_url": "https://example.com/br.png",
                              "category": "디저트/음료"
                            }
                          ]
                        }
                        """
							)
					)
			),
			@ApiResponse(responseCode = "400", description = "잘못된 카테고리")
	})
	@GetMapping("/brands")
	public ResponseEntity<BaseResponse<List<BrandResponse>>> getLifeSubscriptionBrands(
			@Parameter(
					description = "브랜드 카테고리 (선택사항)",
					example = "도서/콘텐츠",
					schema = @Schema(type = "string", allowableValues = {"도서/콘텐츠", "디저트/음료", "편의점/쇼핑", "카페/음료", "베이커리"})
			)
			@RequestParam(required = false) String category
	) {
		log.info("라이프 구독 브랜드 조회 요청 - category: {}", category);

		if (category != null) {
			log.debug("컨트롤러에서 받은 원본 카테고리: '{}'", category);
		}

		List<BrandResponse> brands = subscriptionService.getLifeSubscriptionBrands(category);

		log.info("브랜드 조회 완료 - category: {}, 브랜드 수: {}", category, brands.size());
		return ResponseEntity.ok(success(brands));
	}

	@Operation(
			summary = "구독 상품 가입",
			description = """
            특정 구독 상품에 가입합니다.
            
            **가입 프로세스:**
            1. 로그인 사용자 확인
            2. 구독 상품 유효성 검증
            3. 중복 가입 방지 체크
            4. 결제 정보 확인
            5. 구독 가입 처리
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "구독 가입 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "구독 가입이 완료되었습니다.",
                          "data": {
                            "subscription_combination_id": 123,
                            "brand_id": 1,
                            "price": 17000
                          }
                        }
                        """
							)
					)
			),
			@ApiResponse(responseCode = "400", description = "가입 불가 (이미 가입됨, 잘못된 요청 등)"),
			@ApiResponse(responseCode = "401", description = "인증 필요"),
			@ApiResponse(responseCode = "404", description = "구독 상품 또는 브랜드를 찾을 수 없음")
	})
	@SecurityRequirement(name = "cookieAuth")
	@PostMapping("/subscribe")
	public ResponseEntity<BaseResponse<SubscribeResponse>> subscribe(
			@Parameter(
					description = "구독 가입 요청 정보",
					required = true,
					content = @Content(
							examples = @ExampleObject(
									value = """
                        {
                          "subscription_id": 1,
                          "brand_id": 1
                        }
                        """
							)
					)
			)
			@RequestBody @Valid SubscribeRequest request
	) {
		log.info("구독 가입 요청 - subscriptionId: {}, brandId: {}",
				request.getSubscriptionId(), request.getBrandId());

		String userId = getCurrentUserId();
		SubscribeResponse response = subscriptionService.subscribe(request, userId);

		log.info("구독 가입 완료 - userId: {}, subscriptionCombinationId: {}",
				userId, response.getSubscriptionCombinationId());
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "구독 상품 해지",
			description = """
            현재 가입 중인 구독 상품을 해지합니다.
            
            **해지 프로세스:**
            1. 로그인 사용자 확인
            2. 구독 조합 ID 유효성 검증
            3. 해지 권한 확인 (본인 구독인지)
            4. 구독 해지 처리
            
            **디버깅 정보:**
            - JWT에서 추출된 사용자 ID 로깅
            - User 테이블 조회 결과 로깅
            - UserSubscriptionCombination 조회 결과 로깅
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "구독 해지 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "구독 해지가 완료되었습니다.",
                          "data": {
                            "subscription_combination_id": 123,
                            "message": "구독이 성공적으로 해지되었습니다."
                          }
                        }
                        """
							)
					)
			),
			@ApiResponse(responseCode = "400", description = "해지 불가 (이미 해지됨, 잘못된 요청 등)"),
			@ApiResponse(responseCode = "401", description = "인증 필요"),
			@ApiResponse(responseCode = "403", description = "해지 권한 없음"),
			@ApiResponse(responseCode = "404", description = "구독을 찾을 수 없음")
	})
	@SecurityRequirement(name = "cookieAuth")
	@DeleteMapping("/unsubscribe")
	public ResponseEntity<BaseResponse<UnsubscribeResponse>> unsubscribe(
			@Parameter(
					description = "구독 해지 요청 정보",
					required = true,
					content = @Content(
							examples = @ExampleObject(
									value = """
                        {
                          "subscription_combination_id": 4
                        }
                        """
							)
					)
			)
			@RequestBody @Valid UnsubscribeRequest request
	) {
		log.info("=== 구독 해지 요청 시작 ===");
		log.info("요청된 subscriptionCombinationId: {}", request.getSubscriptionCombinationId());

		// JWT에서 사용자 ID 추출 및 디버깅
		String jwtUserId = getCurrentUserId();
		log.info("JWT에서 추출된 사용자 ID: {}", jwtUserId);

		// User 조회 및 PK 확인
		User user = userRepository.findByUserId(jwtUserId)
				.orElseThrow(() -> {
					log.error("사용자를 찾을 수 없습니다. userId: {}", jwtUserId);
					return new RuntimeException("사용자를 찾을 수 없습니다.");
				});

		Long userPkId = user.getId();
		log.info("User 조회 성공 - JWT userId: {} -> User PK: {}", jwtUserId, userPkId);

		UnsubscribeResponse response = subscriptionService.unsubscribe(request, jwtUserId);

		log.info("구독 해지 완료 - userId: {}, subscriptionCombinationId: {}",
				jwtUserId, response.getSubscriptionCombinationId());
		log.info("=== 구독 해지 요청 완료 ===");

		return ResponseEntity.ok(BaseResponse.success(response));
	}

	/**
	 * SecurityContext에서 현재 사용자 ID 추출
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증되지 않은 사용자의 구독 API 접근 시도");
			throw new RuntimeException("인증되지 않은 사용자입니다.");
		}

		String userId = (String) authentication.getPrincipal();
		log.debug("SecurityContext에서 추출된 사용자 ID: {}", userId);
		return userId;
	}
}