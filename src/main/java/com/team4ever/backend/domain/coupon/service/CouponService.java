package com.team4ever.backend.domain.coupon.service;

import java.util.Map;
import java.util.stream.Collectors;
import com.team4ever.backend.domain.coupon.dto.CouponClaimResponse;
import com.team4ever.backend.domain.coupon.dto.CouponResponse;
import com.team4ever.backend.domain.coupon.dto.CouponUseResponse;
import com.team4ever.backend.domain.coupon.entity.Coupon;
import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import com.team4ever.backend.domain.coupon.repository.CouponRepository;
import com.team4ever.backend.domain.coupon.repository.UserCouponRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;


    public List<CouponResponse> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();

        return coupons.stream()
                .map(c -> CouponResponse.from(c, null)) // user 없이 used 정보는 null 처리
                .collect(Collectors.toList());
    }


    public CouponClaimResponse claimCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("쿠폰 없음"));

        userCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .orElseGet(() -> userCouponRepository.save(new UserCoupon(null, userId, coupon, false)));

        return new CouponClaimResponse(couponId, "성공");
    }

    public CouponUseResponse useCoupon(Long couponId, Long userId) {
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new RuntimeException("쿠폰 미발급"));

        userCoupon.setIsUsed(true);
        userCouponRepository.save(userCoupon);

        return new CouponUseResponse(couponId, true);
    }
}
