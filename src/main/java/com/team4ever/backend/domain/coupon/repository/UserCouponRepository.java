package com.team4ever.backend.domain.coupon.repository;

import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Long couponId);

    // 🔁 userId 기준 제거하고 전체 조회만 남김
    @EntityGraph(attributePaths = "coupon")
    List<UserCoupon> findAll();
}
