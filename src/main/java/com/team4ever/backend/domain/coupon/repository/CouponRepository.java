package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.common.brand.Brand;
import com.team4ever.backend.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {
	@Query("SELECT c FROM Coupon c WHERE :today BETWEEN c.startDate AND c.endDate")
	List<Coupon> findAllValid(LocalDate today);
}