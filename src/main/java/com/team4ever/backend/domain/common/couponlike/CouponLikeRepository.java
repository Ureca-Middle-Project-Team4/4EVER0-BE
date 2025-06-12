package com.team4ever.backend.domain.common.couponlike;

import com.team4ever.backend.domain.user.dto.LikedCouponDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponLikeRepository extends JpaRepository<CouponLike, Integer> {

	/**
	 * 사용자가 좋아요한 쿠폰 목록 조회 (쿠폰 상세 정보 포함)
	 */
	@Query("""
        SELECT new com.team4ever.backend.domain.user.dto.LikedCouponDto(
            c.id,
            c.title,
            c.description,
            c.discountType,
            c.discountValue,
            c.startDate,
            c.endDate,
            b.name,
            b.id
        )
        FROM CouponLike cl
        JOIN Coupon c ON cl.couponId = c.id
        JOIN Brand b ON cl.brandId = b.id
        WHERE cl.userId = :userId AND cl.isLiked = true
        ORDER BY cl.id DESC
    """)
	List<LikedCouponDto> findLikedCouponsByUserId(@Param("userId") Long userId);
}