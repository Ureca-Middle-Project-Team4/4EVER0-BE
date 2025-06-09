package com.team4ever.backend.domain.coupons.controller;

import com.team4ever.backend.domain.coupons.dto.CouponLikeResponseDto;
import com.team4ever.backend.domain.coupons.dto.CouponSummaryDto;
import com.team4ever.backend.domain.coupons.service.CouponService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "coupon-like-controller", description = "쿠폰 좋아요 및 BEST 3 혜택 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
public class CouponLikeController {

    private final CouponService couponService;

    @PostMapping("/{couponId}/like")
    public BaseResponse<CouponLikeResponseDto> likeCoupon(@PathVariable Long couponId) {
        CouponLikeResponseDto result = couponService.likeCoupon(couponId);
        return BaseResponse.success(result);
    }

    @GetMapping("/best")
    public BaseResponse<List<CouponSummaryDto>> getBestCoupons() {
        List<CouponSummaryDto> bestCoupons = couponService.getBestCoupons();
        return BaseResponse.success(bestCoupons);
    }
}
