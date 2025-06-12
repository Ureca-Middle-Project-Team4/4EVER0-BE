package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    // 사용자+쿠폰 단건 조회 (사용 상태 확인)
    @EntityGraph(attributePaths = "coupon")
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Integer couponId);

    boolean existsByUserIdAndCouponId(Long userId, Integer couponId);

    // 사용자별 전체 쿠폰 조회 (isUsed 상태 확인)
    @EntityGraph(attributePaths = "coupon")
    List<UserCoupon> findByUserId(Long userId);
}
