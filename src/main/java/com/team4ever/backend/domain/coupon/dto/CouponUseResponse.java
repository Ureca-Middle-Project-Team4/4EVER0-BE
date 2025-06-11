package com.team4ever.backend.domain.coupon.dto;

import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponUseResponse {
    private Integer couponId;
    private Boolean isUsed;

    public static CouponUseResponse from(UserCoupon uc) {
        return new CouponUseResponse(uc.getCoupon().getId(), uc.getIsUsed());
    }
}