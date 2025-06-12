package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.UserSubscriptionCombination;
import com.team4ever.backend.domain.user.dto.UserSubscriptionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionCombinationRepository extends JpaRepository<UserSubscriptionCombination, Integer> {

	// 기존 메서드들
	boolean existsByUserIdAndSubscriptionCombinationId(Long userId, Integer subscriptionCombinationId);
	Optional<UserSubscriptionCombination> findByUserIdAndSubscriptionCombinationId(Long userId, Integer subscriptionCombinationId);

	// 사용자 구독 목록 조회 (구독 상품명 + 브랜드명)
	@Query("""
        SELECT new com.team4ever.backend.domain.user.dto.UserSubscriptionDto(
            usc.id,
            s.title,
            b.name,
            usc.price,
            usc.createdAt
        )
        FROM UserSubscriptionCombination usc
        JOIN SubscriptionCombination sc ON usc.subscriptionCombinationId = sc.id
        JOIN Subscription s ON sc.subscriptionId = s.id
        JOIN Brand b ON sc.brandId = b.id
        WHERE usc.userId = :userId
        ORDER BY usc.createdAt DESC
    """)
	List<UserSubscriptionDto> findUserSubscriptionsWithDetails(@Param("userId") Long userId);

	// 사용자의 총 구독 수 조회
	long countByUserId(Long userId);
}