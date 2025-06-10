package com.team4ever.backend.domain.subscriptions.service;

import com.team4ever.backend.domain.subscriptions.dto.*;
import com.team4ever.backend.domain.subscriptions.entity.Brand;
import com.team4ever.backend.domain.subscriptions.entity.Subscription;
import com.team4ever.backend.domain.subscriptions.entity.SubscriptionCombination;
import com.team4ever.backend.domain.subscriptions.repository.BrandRepository;
import com.team4ever.backend.domain.subscriptions.repository.SubscriptionCombinationRepository;
import com.team4ever.backend.domain.subscriptions.repository.SubscriptionRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final BrandRepository brandRepository;
	private final SubscriptionCombinationRepository subscriptionCombinationRepository;

	/**
	 * 메인 구독 상품 조회
	 */
	public List<SubscriptionResponse> getMainSubscriptions() {
		return subscriptionRepository.findAll().stream()
				.map(this::toSubscriptionResponse)
				.collect(Collectors.toList());
	}

	/**
	 * 라이프 구독 브랜드 조회 (전체 or 카테고리별)
	 */
	public List<BrandResponse> getLifeSubscriptionBrands(String category) {
		List<Brand> brands;

		if (category != null && !category.trim().isEmpty()) {
			// 디버깅을 위한 로그 추가
			System.out.println("검색하려는 카테고리: '" + category + "'");
			System.out.println("카테고리 길이: " + category.length());

			// URL 디코딩 시도 (필요한 경우)
			String decodedCategory = decodeCategory(category);
			System.out.println("디코딩된 카테고리: '" + decodedCategory + "'");

			brands = brandRepository.findByCategory(decodedCategory);

			// 전체 브랜드 목록도 로그로 확인
			List<Brand> allBrands = brandRepository.findAll();
			System.out.println("전체 브랜드 수: " + allBrands.size());
			allBrands.forEach(brand ->
					System.out.println("브랜드 카테고리: '" + brand.getCategory() + "'")
			);
		} else {
			brands = brandRepository.findAll();
		}

		return brands.stream()
				.map(this::toBrandResponse)
				.collect(Collectors.toList());
	}

	/**
	 * 구독 가입
	 */
	@Transactional
	public SubscribeResponse subscribe(SubscribeRequest request) {
		// 요청 검증
		validateSubscribeRequest(request);

		// 구독 상품 존재 여부 확인
		Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
				.orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

		// 브랜드 존재 여부 확인
		Brand brand = brandRepository.findById(request.getBrandId())
				.orElseThrow(() -> new CustomException(ErrorCode.BRAND_NOT_FOUND));

		Integer currentUserId = getCurrentUserId();

		// 중복 구독 체크
		checkDuplicateSubscription(request.getSubscriptionId(), request.getBrandId(), currentUserId);

		// 구독 조합 생성 및 저장
		SubscriptionCombination combination = SubscriptionCombination.builder()
				.subscriptionId(request.getSubscriptionId())
				.brandId(request.getBrandId())
				.userId(currentUserId)
				.price(subscription.getPrice())  // 구독 상품의 가격으로 설정
				.build();

		SubscriptionCombination savedCombination = subscriptionCombinationRepository.save(combination);

		return SubscribeResponse.builder()
				.subscriptionCombinationId(savedCombination.getId())
				.userId(savedCombination.getUserId())
				.price(savedCombination.getPrice())
				.build();
	}

	private SubscriptionResponse toSubscriptionResponse(Subscription subscription) {
		return SubscriptionResponse.builder()
				.id(subscription.getId())
				.title(subscription.getTitle())
				.imageUrl(subscription.getImageUrl())
				.category(subscription.getCategory())
				.price(subscription.getPrice())
				.build();
	}

	private BrandResponse toBrandResponse(Brand brand) {
		return BrandResponse.builder()
				.id(brand.getId())
				.title(brand.getName())
				.imageUrl(brand.getImageUrl())
				.category(brand.getCategory())
				.build();
	}

	/**
	 * 구독 요청 검증
	 */
	private void validateSubscribeRequest(SubscribeRequest request) {
		if (request.getSubscriptionId() == null || request.getBrandId() == null) {
			throw new CustomException(ErrorCode.INVALID_SUBSCRIPTION_REQUEST);
		}

		if (request.getSubscriptionId() <= 0 || request.getBrandId() <= 0) {
			throw new CustomException(ErrorCode.INVALID_SUBSCRIPTION_REQUEST);
		}
	}

	/**
	 * 중복 구독 체크
	 */
	private void checkDuplicateSubscription(Integer subscriptionId, Integer brandId, Integer userId) {
		boolean exists = subscriptionCombinationRepository.existsBySubscriptionIdAndBrandIdAndUserId(
				subscriptionId, brandId, userId);

		if (exists) {
			throw new CustomException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
		}
	}

	/**
	 * URL 디코딩 처리
	 */
	private String decodeCategory(String category) {
		try {
			// 이미 디코딩된 경우 그대로 반환
			if (!category.contains("%")) {
				return category;
			}
			// URL 디코딩 수행
			return java.net.URLDecoder.decode(category, "UTF-8");
		} catch (Exception e) {
			System.out.println("디코딩 실패: " + e.getMessage());
			return category;
		}
	}

	// TODO: JWT 토큰에서 현재 사용자 ID를 가져오는 로직 구현
	private Integer getCurrentUserId() {
		// 임시로 101 반환 (실제로는 SecurityContext에서 가져와야 함)
		return 101;
	}
}