package com.team4ever.backend.domain.coupon.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponUseRequest {
    private Long couponId;
    private String reason; // 예시: 쿠폰 사용 사유
}
