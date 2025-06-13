package com.team4ever.backend.domain.coupon.controller;

import com.team4ever.backend.domain.coupon.dto.*;
import com.team4ever.backend.domain.coupon.service.CouponService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "전체 쿠폰 조회")
    @GetMapping
    public BaseResponse<List<CouponResponse>> getAllCoupons() {
        // userId 없이 전체 쿠폰만 반환
        return BaseResponse.success(
                couponService.getAllCoupons(null)
        );
    }

    @Operation(summary = "특정 쿠폰 발급 요청")
    @PostMapping("/{couponId}/claim")
    public BaseResponse<CouponClaimResponse> claimCoupon(
            @PathVariable Integer couponId,
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = Long.valueOf(oAuth2User.getAttribute("id").toString());

        CouponClaimResponse response = couponService.claimCoupon(userId, couponId);
        return BaseResponse.success(response);
    }

    @Operation(summary = "특정 쿠폰 사용 처리")
    @PatchMapping("/{couponId}/use")
    public BaseResponse<CouponUseResponse> useCoupon(
            @PathVariable Integer couponId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        if (oauth2User == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object idAttr = oauth2User.getAttribute("id");
        if (idAttr == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = Long.valueOf(idAttr.toString());
        return BaseResponse.success(
                couponService.useCoupon(userId, couponId)
        );
    }

    private Long extractUserId(OAuth2User oauth2User) {
        Object idAttr = oauth2User.getAttribute("id");
        if (idAttr == null) {
            throw new IllegalStateException("OAuth2User에 'id' 속성이 없습니다.");
        }
        try {
            return Long.valueOf(idAttr.toString());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("'id' 속성의 형식이 올바르지 않습니다: " + idAttr);
        }
    }

    @Operation(summary = "쿠폰 좋아요")
    @PostMapping("/{couponId}/like")
    public BaseResponse<CouponLikeResponse> likeCoupon(@PathVariable Long couponId) {
        CouponLikeResponse result = couponService.likeCoupon(couponId);
        return BaseResponse.success(result);
    }

    @Operation(summary = "BEST 3 쿠폰 조회")
    @GetMapping("/best")
    public BaseResponse<List<CouponSummary>> getBestCoupons() {
        List<CouponSummary> result = couponService.getBestCoupons();
        return BaseResponse.success(result);
    }

}
