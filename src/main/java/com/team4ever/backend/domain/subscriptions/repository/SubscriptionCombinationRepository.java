package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.SubscriptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionCombinationRepository extends JpaRepository<SubscriptionCombination, Integer> {

	List<SubscriptionCombination> findByUserId(Integer userId);

	List<SubscriptionCombination> findBySubscriptionId(Integer subscriptionId);

	List<SubscriptionCombination> findByBrandId(Integer brandId);

	// This must be Integer userId to match the service's conversion
	Optional<SubscriptionCombination> findBySubscriptionIdAndBrandIdAndUserId(
			Integer subscriptionId, Integer brandId, Integer userId); // Changed from String to Integer
}