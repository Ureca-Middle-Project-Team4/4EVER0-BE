package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.SubscriptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionCombinationRepository extends JpaRepository<SubscriptionCombination, Integer> {
	Optional<SubscriptionCombination> findBySubscriptionIdAndBrandIdAndUserId(
			Integer subscriptionId, Integer brandId, Integer userId);
}