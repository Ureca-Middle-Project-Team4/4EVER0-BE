package com.team4ever.backend.domain.plan.controller;

import com.team4ever.backend.domain.plan.dto.PlanChangeRequest;
import com.team4ever.backend.domain.plan.dto.PlanChangeResponse;
import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.service.PlanService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "사용자 요금제 API", description = "사용자 개인 요금제 관리 API")
public class UserPlanController {

	private final PlanService planService;

	@Operation(summary = "내가 사용중인 요금제 조회")
	@GetMapping
	public ResponseEntity<BaseResponse<PlanResponse>> getCurrentPlan(
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();
		PlanResponse response = planService.getCurrentPlan(oauthUserId);

		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(summary = "요금제 변경")
	@PostMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> changePlan(
			@Valid @RequestBody PlanChangeRequest request,
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();
		PlanChangeResponse response = planService.changePlan(request, oauthUserId);

		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(summary = "요금제 해지")
	@DeleteMapping
	public ResponseEntity<BaseResponse<PlanChangeResponse>> cancelPlan(
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