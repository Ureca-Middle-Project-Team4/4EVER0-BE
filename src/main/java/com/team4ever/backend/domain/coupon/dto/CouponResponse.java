package com.team4ever.backend.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.team4ever.backend.domain.coupon.entity.Coupon;


import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String title;
    private String description;
    private String brand;
    private String discountType;
    private Integer discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isUsed;

    public static CouponResponse from(Coupon coupon, Boolean used) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getTitle(),
                coupon.getDescription(),
                coupon.getBrand(),
                coupon.getDiscountType().name(),
                coupon.getDiscountValue(),
                coupon.getStartDate(),
                coupon.getEndDate(),
                used
        );
    }
}
