package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.SubscriptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionCombinationRepository extends JpaRepository<SubscriptionCombination, Integer> {

	// 사용자 ID로 조회
	List<SubscriptionCombination> findByUserId(Integer userId);

	// 구독 ID로 조회
	List<SubscriptionCombination> findBySubscriptionId(Integer subscriptionId);

	// 브랜드 ID로 조회
	List<SubscriptionCombination> findByBrandId(Integer brandId);

	// 중복 구독 체크를 위한 메서드
	boolean existsBySubscriptionIdAndBrandIdAndUserId(Integer subscriptionId, Integer brandId, Integer userId);
}