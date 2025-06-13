package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

	@Query("SELECT c FROM Coupon c WHERE :today BETWEEN c.startDate AND c.endDate")
	List<Coupon> findAllValid(LocalDate today);

	// 좋아요 상위 3개 쿠폰 조회 (native 쿼리 사용)
	@Query(value = """
        SELECT c.* FROM coupons c
        JOIN (
            SELECT coupon_id
            FROM coupon_likes
            WHERE is_liked = true
            GROUP BY coupon_id
            ORDER BY COUNT(*) DESC
            LIMIT 3
        ) cl ON c.id = cl.coupon_id
    """, nativeQuery = true)
	List<Coupon> findTop3ByLikeCount();
}