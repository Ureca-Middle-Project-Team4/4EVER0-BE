package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.UserSubscriptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionCombinationRepository extends JpaRepository<UserSubscriptionCombination, Integer> {
	boolean existsByUserIdAndSubscriptionCombinationId(Long userId, Integer subscriptionCombinationId);
}