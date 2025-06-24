package com.team4ever.backend.domain.coupon.dto;

import com.team4ever.backend.domain.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private Integer id;
    private String title;
    private String description;
    private String brand;
    private String discountType;
    private Integer discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isUsed;

    public static CouponResponse from(Coupon c, boolean used) {
        return new CouponResponse(
                c.getId(),
                c.getTitle(),
                c.getDescription(),
                c.getBrand().getName(),
                c.getDiscountType().name(),
                c.getDiscountValue(),
                c.getStartDate(),
                c.getEndDate(),
                used
        );
    }
}
