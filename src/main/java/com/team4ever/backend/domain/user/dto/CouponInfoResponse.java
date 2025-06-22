package com.team4ever.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CouponInfoResponse(
        @JsonProperty("coupon_id")
        Long couponId,

        @JsonProperty("title")
        String title,

        @JsonProperty("brand")
        BrandInfo brand,

        @JsonProperty("is_used")
        boolean isUsed,

        @JsonProperty("start_date")
        String startDate,

        @JsonProperty("end_date")
        String endDate
) {
    public record BrandInfo(
            @JsonProperty("id")
            Long id,

            @JsonProperty("name")
            String name,

            @JsonProperty("image_url")
            String imageUrl
    ) {}
}
