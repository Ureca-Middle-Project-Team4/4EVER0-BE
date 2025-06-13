package com.team4ever.backend.domain.plan.controller;

import com.team4ever.backend.domain.plan.dto.PlanChangeRequest;
import com.team4ever.backend.domain.plan.dto.PlanChangeResponse;
import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.service.PlanService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user/plans")
@RequiredArgsConstructor
@Tag(name = "사용자 요금제 API", description = "사용자 개인 요금제 관리 API (인증 필요)")
@SecurityRequirement(name = "bearerAuth")
public class UserPlanController {

	private final PlanService planService;

	@Operation(
			summary = "내가 사용중인 요금제 조회",
			description = "현재 로그인한 사용자가 사용중인 요금제 정보를 조회합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "사용자 요금제 조회 성공",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "사용자 또는 요금제를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<BaseResponse<PlanResponse>> getCurrentPlan(
			@Parameter(hidden = true)
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();
		PlanResponse response = planService.getCurrentPlan(oauthUserId);

		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "요금제 변경",
			description = "현재 로그인한 사용자의 요금제를 변경합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 변경 성공",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "잘못된 요청 (이미 사용중인 요금제 등)",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "사용자 또는 요금제를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			)
	})
	@PostMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> changePlan(
			@Parameter(
					description = "요금제 변경 요청 정보",
					required = true
			)
			@Valid @RequestBody PlanChangeRequest request,
			@Parameter(hidden = true)
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();
		PlanChangeResponse response = planService.changePlan(request, oauthUserId);

		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "요금제 해지",
			description = "현재 로그인한 사용자의 요금제를 해지합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 해지 성공",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "400",
					description = "해지할 요금제가 없음",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "401",
					description = "인증되지 않은 사용자",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "사용자를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			)
	})
	@DeleteMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> cancelPlan(
			@Parameter(hidden = true)
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();
		PlanChangeResponse response = planService.cancelPlan(oauthUserId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}