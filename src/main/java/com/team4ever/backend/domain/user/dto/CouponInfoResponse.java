package com.team4ever.backend.domain.user.dto;

public record CouponInfoResponse(
    Long couponId,
    String title,
    BrandInfo brand,
    boolean isUsed,
    String startDate,
    String endDate
) {
    public record BrandInfo(Long id, String name, String imageUrl) {}
}
