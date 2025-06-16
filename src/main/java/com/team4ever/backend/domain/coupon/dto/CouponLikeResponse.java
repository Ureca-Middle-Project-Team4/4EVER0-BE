package com.team4ever.backend.domain.coupon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "쿠폰 좋아요 응답 DTO")
public class CouponLikeResponse {

    @Schema(description = "좋아요 여부")
    private boolean liked;

    @Schema(description = "쿠폰 ID")
    @JsonProperty("coupon_id")
    private Long couponId;

    public static CouponLikeResponse of(Long couponId) {
        return new CouponLikeResponse(true, couponId);
    }
}


