package com.team4ever.backend.domain.coupon.controller;

import com.team4ever.backend.domain.coupon.dto.CouponClaimResponse;
import com.team4ever.backend.domain.coupon.dto.CouponResponse;
import com.team4ever.backend.domain.coupon.dto.CouponUseResponse;
import com.team4ever.backend.domain.coupon.service.CouponService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "전체 쿠폰 조회")
    @GetMapping("")
    public BaseResponse<List<CouponResponse>> getAllCoupons() {
        return BaseResponse.success(couponService.getAllCoupons());
    }


    @Operation(summary = "특정 쿠폰 발급 요청")
    @GetMapping("/{couponId}/claim")
    public BaseResponse<CouponClaimResponse> claimCoupon(
            @PathVariable Long couponId,
            @RequestParam Long userId // 🔁 인증 제거
    ) {
        return BaseResponse.success(couponService.claimCoupon(userId, couponId));
    }

    @Operation(summary = "특정 쿠폰 사용 처리")
    @PatchMapping("/{couponId}/use")
    public BaseResponse<CouponUseResponse> useCoupon(
            @PathVariable Long couponId,
            @RequestParam Long userId // 🔁 인증 제거
    ) {
        return BaseResponse.success(couponService.useCoupon(userId, couponId));
    }
}
