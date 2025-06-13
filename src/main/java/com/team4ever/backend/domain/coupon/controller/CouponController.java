package com.team4ever.backend.domain.coupon.controller;

import com.team4ever.backend.domain.common.couponlike.CouponLike;
import com.team4ever.backend.domain.common.couponlike.CouponLikeRepository;
import com.team4ever.backend.domain.coupon.dto.*;
import com.team4ever.backend.domain.coupon.entity.Coupon;
import com.team4ever.backend.domain.coupon.repository.CouponRepository;
import com.team4ever.backend.domain.coupon.service.CouponService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final CouponLikeRepository couponLikeRepository;
    private final CouponRepository couponRepository;

    @Operation(summary = "전체 쿠폰 조회")
    @GetMapping
    public BaseResponse<List<CouponResponse>> getAllCoupons() {
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

    @Operation(summary = "쿠폰 좋아요 등록")
    @PostMapping("/{couponId}/like")
    public BaseResponse<CouponLikeResponse> likeCouponApi(
            @PathVariable Integer couponId,
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        Long userId = extractUserId(oAuth2User);
        Integer brandId = 1; // TODO: 브랜드 ID 동적 처리 필요 시 수정
        return BaseResponse.success(likeCoupon(couponId, userId.intValue(), brandId));
    }

    @Operation(summary = "좋아요 많은 BEST 쿠폰 Top3 조회")
    @GetMapping("/best")
    public BaseResponse<List<CouponSummary>> getBestCouponsApi() {
        return BaseResponse.success(getBestCoupons());
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

    @Transactional
    public CouponLikeResponse likeCoupon(Integer couponId, Integer userId, Integer brandId) {
        CouponLike like = couponLikeRepository.findByCouponIdAndUserId(couponId, userId.longValue())
                .orElse(null);

        if (like != null) {
            if (like.isLiked()) {
                throw new CustomException(ErrorCode.COUPON_ALREADY_LIKED);
            }
            like.like();
        } else {
            couponLikeRepository.save(CouponLike.create(couponId, userId, brandId));
        }

        return new CouponLikeResponse(true, Long.valueOf(couponId));
    }

    @Transactional(readOnly = true)
    public List<CouponSummary> getBestCoupons() {
        List<Coupon> topCoupons = couponRepository.findTop3ByLikeCount();
        return topCoupons.stream()
                .map(CouponSummary::from)
                .collect(Collectors.toList());
    }
}
