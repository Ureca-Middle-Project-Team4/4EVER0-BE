package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.UserSubscriptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionCombinationRepository extends JpaRepository<UserSubscriptionCombination, Integer> {
	// 이 메서드를 추가하여 특정 사용자가 특정 구독 조합을 이미 구독했는지 확인합니다.
	boolean existsByUserIdAndSubscriptionCombinationId(Integer userId, Integer subscriptionCombinationId);
}