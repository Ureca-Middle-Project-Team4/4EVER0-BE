package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
	List<Subscription> findByCategory(String category);
}