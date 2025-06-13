package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.coupon.entity.CouponLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponLikeRepository extends JpaRepository<CouponLike, Long> {
    Optional<CouponLike> findByCouponIdAndUserId(Integer couponId, Integer userId);
}
