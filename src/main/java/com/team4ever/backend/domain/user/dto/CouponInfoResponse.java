package com.team4ever.backend.domain.user.dto;

public record CouponInfoResponse(
    Long couponId,
    String title,
    BrandInfo brand,
    boolean isUsed
) {
    public record BrandInfo(Long id, String name, String imageUrl) {}
}
