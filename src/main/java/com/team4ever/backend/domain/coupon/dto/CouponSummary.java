package com.team4ever.backend.domain.coupon.dto;

import com.team4ever.backend.domain.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class CouponSummary {
    private Integer id;
    private String title;
    private String description;
    private String brand;
    private String discountType;
    private int discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private int likes;

    public static CouponSummary from(Coupon coupon) {
        return CouponSummary.builder()
                .id(coupon.getId())
                .title(coupon.getTitle())
                .description(coupon.getDescription())
                .brand(coupon.getBrand().getName())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .likes(coupon.getLikes())
                .build();
    }
}
