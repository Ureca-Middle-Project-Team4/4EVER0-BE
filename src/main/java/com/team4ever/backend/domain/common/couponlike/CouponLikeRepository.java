package com.team4ever.backend.domain.common.couponlike;

import com.team4ever.backend.domain.user.dto.LikedCouponDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponLikeRepository extends JpaRepository<CouponLike, Integer> {

	// 사용자가 좋아요한 쿠폰 목록 조회
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

	// 사용자의 특정 쿠폰 좋아요 여부 조회
	Optional<CouponLike> findByCouponIdAndUserId(Integer couponId, Long userId);

	// 현재 좋아요 중인지 확인
	@Query("""
        SELECT cl FROM CouponLike cl
        WHERE cl.couponId = :couponId
          AND cl.userId = :userId
          AND cl.isLiked = true
    """)
	Optional<CouponLike> findActiveLike(@Param("couponId") Integer couponId, @Param("userId") Long userId);

	// 좋아요 수가 많은 쿠폰 ID Top 3 조회
	@Query(value = """
        SELECT coupon_id
        FROM coupon_likes
        WHERE is_liked = true
        GROUP BY coupon_id
        ORDER BY COUNT(*) DESC
        LIMIT 3
    """, nativeQuery = true)
	List<Integer> findTop3CouponIdsByLikeCount();
}
