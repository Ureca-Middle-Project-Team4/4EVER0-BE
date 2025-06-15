package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	// 유효 기간 내 전체 쿠폰 조회
	@Query("SELECT c FROM Coupon c WHERE :today BETWEEN c.startDate AND c.endDate")
	List<Coupon> findAllValid(LocalDate today);

	// 특정 쿠폰 ID 리스트에 해당하는 쿠폰들 조회
	List<Coupon> findByIdIn(List<Integer> ids);
}
