package com.team4ever.backend.domain.coupon.dto;

import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponClaimResponse {
    private Integer couponId;

    public static CouponClaimResponse from(UserCoupon uc) {
        return new CouponClaimResponse(uc.getCoupon().getId());
    }
}