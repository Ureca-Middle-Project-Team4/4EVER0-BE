package com.team4ever.backend.domain.subscriptions.service;

import com.team4ever.backend.domain.common.brand.Brand;
import com.team4ever.backend.domain.common.brand.BrandRepository;
import com.team4ever.backend.domain.subscriptions.dto.*;
import com.team4ever.backend.domain.subscriptions.entity.Subscription;
import com.team4ever.backend.domain.subscriptions.entity.SubscriptionCombination;
import com.team4ever.backend.domain.subscriptions.entity.UserSubscriptionCombination;
import com.team4ever.backend.domain.subscriptions.repository.SubscriptionCombinationRepository;
import com.team4ever.backend.domain.subscriptions.repository.SubscriptionRepository;
import com.team4ever.backend.domain.subscriptions.repository.UserSubscriptionCombinationRepository;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

	private final SubscriptionRepository subscriptionRepository;
	private final SubscriptionCombinationRepository subscriptionCombinationRepository;
	private final UserSubscriptionCombinationRepository userSubscriptionCombinationRepository;
	private final UserRepository userRepository;
	private final BrandRepository brandRepository;

	public List<SubscriptionResponse> getMainSubscriptions() {
		return subscriptionRepository.findAll().stream()
				.map(this::toSubscriptionResponse)
				.collect(Collectors.toList());
	}

	public List<BrandResponse> getLifeSubscriptionBrands(String category) {
		List<Brand> brands;

		if (category != null && !category.trim().isEmpty()) {
			log.debug("검색하려는 카테고리: '{}'", category);
			String decodedCategory = decodeCategory(category);
			log.debug("디코딩된 카테고리: '{}'", decodedCategory);
			brands = brandRepository.findByCategory(decodedCategory);
		} else {
			brands = brandRepository.findAll();
		}

		return brands.stream()
				.map(this::toBrandResponse)
				.collect(Collectors.toList());
	}

	@Transactional
	public SubscribeResponse subscribe(SubscribeRequest request, String oauthUserId) {
		try {
			log.info("구독 가입 요청 시작 - subscriptionId: {}, brandId: {}, oauthUserId: {}",
					request.getSubscriptionId(), request.getBrandId(), oauthUserId);

			validateSubscribeRequest(request);

			Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
					.orElseThrow(() -> {
						log.error("구독 상품을 찾을 수 없습니다. ID: {}", request.getSubscriptionId());
						return new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND);
					});

			Brand brand = brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> {
						log.error("브랜드를 찾을 수 없습니다. ID: {}", request.getBrandId());
						return new CustomException(ErrorCode.BRAND_NOT_FOUND);
					});

			// OAuth userId로 실제 User 엔티티 조회
			User currentUser = userRepository.findByUserId(oauthUserId)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			// 실제 PK 사용
			Long userId = currentUser.getId();
			log.info("현재 사용자 PK: {}", userId);

			Optional<SubscriptionCombination> existingCombination =
					subscriptionCombinationRepository.findBySubscriptionIdAndBrandIdAndUserId(
							request.getSubscriptionId(), request.getBrandId(), userId);

			SubscriptionCombination savedCombination;
			if (existingCombination.isPresent()) {
				savedCombination = existingCombination.get();
				log.info("기존 구독 조합 발견 - combinationId: {}", savedCombination.getId());
			} else {
				SubscriptionCombination newCombination = SubscriptionCombination.builder()
						.subscriptionId(subscription.getId())
						.brandId(brand.getId())
						.userId(userId)
						.price(subscription.getPrice())
						.build();
				savedCombination = subscriptionCombinationRepository.save(newCombination);
				log.info("새 구독 조합 저장 완료 - combinationId: {}", savedCombination.getId());
			}

			boolean userSubscriptionExists = userSubscriptionCombinationRepository
					.existsByUserIdAndSubscriptionCombinationId(userId, savedCombination.getId());

			if (userSubscriptionExists) {
				log.warn("사용자가 이미 해당 구독 조합을 구독 중입니다. - userId: {}, subscriptionCombinationId: {}",
						userId, savedCombination.getId());
				throw new CustomException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS);
			}

			UserSubscriptionCombination userSubscription = UserSubscriptionCombination.builder()
					.userId(userId)
					.subscriptionCombinationId(savedCombination.getId())
					.price(savedCombination.getPrice())
					.build();

			userSubscriptionCombinationRepository.save(userSubscription);
			log.info("사용자 구독 내역 저장 완료 - userSubscriptionCombinationId: {}", userSubscription.getId());

			return SubscribeResponse.builder()
					.subscriptionCombinationId(savedCombination.getId())
					.brandId(savedCombination.getBrandId())
					.price(savedCombination.getPrice())
					.build();

		} catch (CustomException e) {
			log.error("구독 가입 중 알려진 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("구독 가입 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	public UnsubscribeResponse unsubscribe(UnsubscribeRequest request, String oauthUserId) {
		try {
			log.info("구독 해지 요청 시작 - subscriptionCombinationId: {}, oauthUserId: {}",
					request.getSubscriptionCombinationId(), oauthUserId);

			// 해지 요청 유효성 검사
			if (request.getSubscriptionCombinationId() == null || request.getSubscriptionCombinationId() <= 0) {
				log.error("유효하지 않은 구독 조합 ID: {}", request.getSubscriptionCombinationId());
				throw new CustomException(ErrorCode.INVALID_SUBSCRIPTION_REQUEST);
			}

			// 사용자 조회
			User currentUser = userRepository.findByUserId(oauthUserId)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			Long userId = currentUser.getId();
			log.info("해지 요청 사용자 PK: {}", userId);

			// 사용자의 구독 내역 조회
			UserSubscriptionCombination userSubscription = userSubscriptionCombinationRepository
					.findByUserIdAndSubscriptionCombinationId(userId, request.getSubscriptionCombinationId())
					.orElseThrow(() -> {
						log.error("구독하지 않은 상품입니다. userId: {}, subscriptionCombinationId: {}",
								userId, request.getSubscriptionCombinationId());
						return new CustomException(ErrorCode.SUBSCRIPTION_NOT_SUBSCRIBED);
					});

			log.info("해지할 구독 내역 조회 완료 - userSubscriptionId: {}, price: {}",
					userSubscription.getId(), userSubscription.getPrice());

			// 응답용 데이터 미리 저장 (삭제 전에)
			Integer subscriptionCombinationId = userSubscription.getSubscriptionCombinationId();

			// 물리적 삭제
			userSubscriptionCombinationRepository.delete(userSubscription);
			log.info("구독 해지 완료 - subscriptionCombinationId: {}, userId: {}",
					subscriptionCombinationId, userId);

			return UnsubscribeResponse.builder()
					.subscriptionCombinationId(subscriptionCombinationId)
					.message("구독이 성공적으로 해지되었습니다.")
					.build();

		} catch (CustomException e) {
			log.error("구독 해지 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("구독 해지 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.SUBSCRIPTION_CANCELLATION_FAILED);
		}
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

	private void validateSubscribeRequest(SubscribeRequest request) {
		if (request.getSubscriptionId() == null || request.getBrandId() == null) {
			log.error("구독 요청 필수 파라미터 누락 - subscriptionId: {}, brandId: {}",
					request.getSubscriptionId(), request.getBrandId());
			throw new CustomException(ErrorCode.INVALID_SUBSCRIPTION_REQUEST);
		}

		if (request.getSubscriptionId() <= 0 || request.getBrandId() <= 0) {
			log.error("구독 요청 파라미터 값이 유효하지 않음 - subscriptionId: {}, brandId: {}",
					request.getSubscriptionId(), request.getBrandId());
			throw new CustomException(ErrorCode.INVALID_SUBSCRIPTION_REQUEST);
		}
	}

	private String decodeCategory(String category) {
		try {
			if (!category.contains("%")) {
				return category;
			}
			return java.net.URLDecoder.decode(category, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.warn("카테고리 디코딩 실패: {}", e.getMessage());
			return category;
		}
	}
}