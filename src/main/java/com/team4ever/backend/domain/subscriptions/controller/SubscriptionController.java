package com.team4ever.backend.domain.subscriptions.controller;

import com.team4ever.backend.domain.subscriptions.dto.BrandResponse;
import com.team4ever.backend.domain.subscriptions.dto.SubscribeRequest;
import com.team4ever.backend.domain.subscriptions.dto.SubscribeResponse;
import com.team4ever.backend.domain.subscriptions.dto.SubscriptionResponse;
import com.team4ever.backend.domain.subscriptions.service.SubscriptionService;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

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
		return ResponseEntity.ok(BaseResponse.success(
				subscriptionService.getMainSubscriptions(),
				"메인 구독 상품 조회 성공"
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

		return ResponseEntity.ok(BaseResponse.success(
				subscriptionService.getLifeSubscriptionBrands(category),
				"라이프 구독 브랜드 조회 성공"
		));
	}


	/**
	 * 구독 가입
	 */
	@PostMapping("/subscribe")
	public ResponseEntity<BaseResponse<SubscribeResponse>> subscribe(
			@Valid @RequestBody SubscribeRequest request) {
		return ResponseEntity.ok(BaseResponse.success(
				subscriptionService.subscribe(request),
				"구독 성공"
		));
	}
}