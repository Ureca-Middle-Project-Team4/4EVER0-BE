package com.team4ever.backend.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponLikeResponse {
    private boolean liked;
    private Long couponId;

    public static CouponLikeResponse of(Long couponId) {
        return new CouponLikeResponse(true, couponId);
    }
}

