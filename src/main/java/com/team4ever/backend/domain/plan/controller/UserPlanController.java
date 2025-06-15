package com.team4ever.backend.domain.plan.controller;

import com.team4ever.backend.domain.plan.dto.PlanChangeRequest;
import com.team4ever.backend.domain.plan.dto.PlanChangeResponse;
import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.service.PlanService;
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

@Slf4j
@RestController
@RequestMapping("/api/user/plans")
@RequiredArgsConstructor
@Tag(name = "사용자 요금제 API", description = "사용자 개인 요금제 관리 API (인증 필요)")
@SecurityRequirement(name = "cookieAuth")  // bearerAuth → cookieAuth로 변경
public class UserPlanController {

	private final PlanService planService;

	@Operation(
			summary = "내가 사용중인 요금제 조회",
			description = """
            현재 로그인한 사용자가 사용중인 요금제 정보를 조회합니다.
            
            **반환 정보:**
            - 현재 활성 요금제 상세 정보
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "사용자 요금제 조회 성공",
					content = @Content(
							mediaType = "application/json",
							schema = @Schema(implementation = BaseResponse.class),
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "사용자 요금제 조회 성공",
                          "data": {
                            "id": 2,
                            "name": "프리미엄 요금제",
                            "price": "19900",
                            "description": "모든 기능을 이용할 수 있는 프리미엄 요금제입니다.",
                            "data": "무제한",
                            "speed": "5G",
                            "sms": "무제한",
                            "voice": "무제한",
                            "share_data": "10GB"
                          }
                        }
                        """
							)
					)
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": false,
                          "message": "인증이 필요합니다.",
                          "data": null
                        }
                        """
							)
					)
			),
			@ApiResponse(
					responseCode = "404",
					description = "사용자 또는 요금제를 찾을 수 없음"
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류"
			)
	})
	@GetMapping
	public ResponseEntity<BaseResponse<PlanResponse>> getCurrentPlan() {
		log.info("사용자 요금제 조회 요청");

		String userId = getCurrentUserId();
		PlanResponse response = planService.getCurrentPlan(userId);

		log.info("사용자 요금제 조회 완료 - userId: {}, planName: {}", userId, response.getName());
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "요금제 변경",
			description = """
            현재 로그인한 사용자의 요금제를 변경합니다.
            
            **변경 프로세스:**
            1. 기존 요금제 확인
            2. 새 요금제 유효성 검증
            3. 결제 정보 확인
            4. 요금제 변경 처리
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 변경 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "요금제 변경이 완료되었습니다.",
                          "data": {
                            "plan_id": 2,
                            "plan_name": "프리미엄 요금제",
                            "changed_at": "2025-06-15T10:30:00",
                            "message": "요금제가 성공적으로 변경되었습니다."
                          }
                        }
                        """
							)
					)
			),
			@ApiResponse(responseCode = "400", description = "잘못된 요청 (이미 사용중인 요금제 등)"),
			@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
			@ApiResponse(responseCode = "404", description = "사용자 또는 요금제를 찾을 수 없음"),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@PostMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> changePlan(
			@Parameter(description = "요금제 변경 요청 정보", required = true)
			@Valid @RequestBody PlanChangeRequest request
	) {
		log.info("요금제 변경 요청 - planId: {}", request.getPlanId());

		String userId = getCurrentUserId();
		PlanChangeResponse response = planService.changePlan(request, userId);

		log.info("요금제 변경 완료 - userId: {}, newPlan: {}", userId, response.getPlanName());
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "요금제 해지",
			description = """
            현재 로그인한 사용자의 요금제를 해지합니다.
            """
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 해지 성공",
					content = @Content(
							mediaType = "application/json",
							examples = @ExampleObject(
									value = """
                        {
                          "success": true,
                          "message": "요금제 해지가 완료되었습니다.",
                          "data": {
                            "plan_id": null,
                            "plan_name": "해지됨",
                            "changed_at": "2025-06-15T10:30:00",
                            "message": "요금제가 성공적으로 해지되었습니다."
                          }
                        }
                        """
							)
					)
			),
			@ApiResponse(responseCode = "400", description = "해지할 요금제가 없음"),
			@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
			@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
			@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	@DeleteMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> cancelPlan() {
		log.info("요금제 해지 요청");

		String userId = getCurrentUserId();
		PlanChangeResponse response = planService.cancelPlan(userId);

		log.info("요금제 해지 완료 - userId: {}, canceledPlan: {}", userId, response.getPlanName());
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	/**
	 * SecurityContext에서 현재 사용자 ID 추출
	 */
	private String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			log.error("인증되지 않은 사용자의 접근 시도");
			throw new RuntimeException("인증되지 않은 사용자입니다.");
		}

		String userId = (String) authentication.getPrincipal();
		log.debug("현재 사용자 ID: {}", userId);

		return userId;
	}
}