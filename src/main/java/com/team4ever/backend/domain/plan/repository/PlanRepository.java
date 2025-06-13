package com.team4ever.backend.domain.plan.repository;

import com.team4ever.backend.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {

	/**
	 * 활성화된 요금제만 조회
	 */
	List<Plan> findByIsActiveTrue();

	/**
	 * 활성화된 특정 요금제 조회
	 */
	Optional<Plan> findByIdAndIsActiveTrue(Integer id);
}
