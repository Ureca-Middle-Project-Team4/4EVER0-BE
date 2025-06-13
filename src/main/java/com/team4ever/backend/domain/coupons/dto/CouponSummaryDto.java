package com.team4ever.backend.domain.coupons.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "쿠폰 요약 응답 DTO")
public class CouponSummaryDto {
    private Long id;
    private String title;
    private String description;
    private String brand;
    private String discountType;
    private int discountValue;
    private String startDate;
    private String endDate;
}
