package com.team4ever.backend.domain.coupon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CouponClaimRequest {
    private Long couponId;
    private Long userId;

}
