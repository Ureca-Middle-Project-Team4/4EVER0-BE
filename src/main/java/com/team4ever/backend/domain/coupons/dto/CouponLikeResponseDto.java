package com.team4ever.backend.domain.coupons.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponLikeResponseDto {
    private boolean liked;
    private Long couponId;
}
