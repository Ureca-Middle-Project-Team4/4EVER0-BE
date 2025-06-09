package com.team4ever.backend.domain.coupon.dto;

import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponClaimResponse {
    private Long couponId;
    private String message;

    public static CouponClaimResponse from(UserCoupon uc) {
        return new CouponClaimResponse(uc.getCoupon().getId(), "쿠폰이 성공적으로 발급되었습니다.");
    }
}
