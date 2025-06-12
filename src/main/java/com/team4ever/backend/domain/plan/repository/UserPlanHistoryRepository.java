package com.team4ever.backend.domain.plan.repository;

import com.team4ever.backend.domain.plan.entity.UserPlanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlanHistoryRepository extends JpaRepository<UserPlanHistory, Integer> {

	/**
	 * 사용자의 최근 요금제 변경 이력 조회
	 */
	@Query("SELECT h FROM UserPlanHistory h WHERE h.userId = :userId ORDER BY h.changedAt DESC")
	List<UserPlanHistory> findByUserIdOrderByChangedAtDesc(@Param("userId") Long userId);

	/**
	 * 사용자의 가장 최근 요금제 이력 조회
	 */
	@Query("SELECT h FROM UserPlanHistory h WHERE h.userId = :userId AND h.actionType = 'CHANGE' ORDER BY h.changedAt DESC LIMIT 1")
	Optional<UserPlanHistory> findLatestPlanByUserId(@Param("userId") Long userId);
}