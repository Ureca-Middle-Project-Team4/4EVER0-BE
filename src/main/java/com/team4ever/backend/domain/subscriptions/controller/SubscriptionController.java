package com.team4ever.backend.domain.subscriptions.controller;

import com.team4ever.backend.domain.subscriptions.dto.*;
import com.team4ever.backend.domain.subscriptions.service.SubscriptionService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import static com.team4ever.backend.global.response.BaseResponse.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

	private final SubscriptionService subscriptionService;

	/**
	 * 메인 구독 상품 조회
	 */
	@GetMapping("/main")
	public ResponseEntity<BaseResponse<List<SubscriptionResponse>>> getMainSubscriptions() {
		return ResponseEntity.ok(success(
				subscriptionService.getMainSubscriptions()
		));
	}

	/**
	 * 라이프 구독 브랜드 조회 (전체 or 카테고리별)
	 */
	@GetMapping("/brands")
	public ResponseEntity<BaseResponse<List<BrandResponse>>> getLifeSubscriptionBrands(
			@RequestParam(required = false) String category) {

		// 디버깅용 로그
		if (category != null) {
			System.out.println("컨트롤러에서 받은 원본 카테고리: '" + category + "'");
		}

		return ResponseEntity.ok(success(
				subscriptionService.getLifeSubscriptionBrands(category)
		));
	}


	/**
	 * 구독 가입
	 */
	@PostMapping("/subscribe")
	public ResponseEntity<BaseResponse<SubscribeResponse>> subscribe(
			@RequestBody SubscribeRequest request,
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		// OAuth에서 받은 사용자 ID를 String으로 처리
		String oauthUserId = oAuth2User.getAttribute("id").toString();

		SubscribeResponse response = subscriptionService.subscribe(request, oauthUserId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}


	/**
	 * 구독 해지
	 */
	@DeleteMapping("/unsubscribe")
	public ResponseEntity<BaseResponse<UnsubscribeResponse>> unsubscribe(
			@RequestBody UnsubscribeRequest request,
			@AuthenticationPrincipal OAuth2User oAuth2User
	) {
		if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		String oauthUserId = oAuth2User.getAttribute("id").toString();

		UnsubscribeResponse response = subscriptionService.unsubscribe(request, oauthUserId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}