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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
        return BaseResponse.success(couponService.getAllCoupons(null));
    }

    @Operation(summary = "특정 쿠폰 발급 요청")
    @PostMapping("/{couponId}/claim")
    public BaseResponse<CouponClaimResponse> claimCoupon(
            @PathVariable Integer couponId,
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        Long userId = extractUserId(oAuth2User);
        CouponClaimResponse response = couponService.claimCoupon(userId, couponId);
        return BaseResponse.success(response);
    }

    @Operation(summary = "특정 쿠폰 사용 처리")
    @PatchMapping("/{couponId}/use")
    public BaseResponse<CouponUseResponse> useCoupon(
            @PathVariable Integer couponId,
            @AuthenticationPrincipal OAuth2User oauth2User
    ) {
        Long userId = extractUserId(oauth2User);
        return BaseResponse.success(couponService.useCoupon(userId, couponId));
    }

    @Operation(summary = "쿠폰 좋아요 등록", responses = {
            @ApiResponse(responseCode = "200", description = "좋아요 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CouponLikeResponse.class),
                            examples = @ExampleObject(value = """
                                {
                                  "status": 200,
                                  "message": "좋아요가 등록되었습니다.",
                                  "data": {
                                    "liked": true,
                                    "coupon_id": 42
                                  }
                                }
                            """)
                    )
            )
    })
//
    @PostMapping("/{couponId}/like")
    public BaseResponse<CouponLikeResponse> likeCoupon(
            @PathVariable Integer couponId,
            @RequestHeader(value = "X-USER-ID", required = false) Long testUserId,
            @RequestHeader(value = "X-BRAND-ID", required = false) Integer testBrandId,
            @AuthenticationPrincipal OAuth2User oauth2User) {

        Long userId = (oauth2User != null) ? Long.valueOf(oauth2User.getAttribute("id")) : testUserId;
        Integer brandId = (oauth2User != null) ? oauth2User.getAttribute("brandId") : testBrandId;

        if (userId == null || brandId == null) {
            throw new IllegalStateException("유효한 ACCESS_TOKEN이 필요하거나 테스트 헤더가 필요합니다.");
        }

        CouponLikeResponse result = couponService.likeCoupon(couponId, userId, brandId);
        return BaseResponse.success(result);
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
        Long userIdLong = userId.longValue();

        // 좋아요 존재 여부 확인 (isLiked=true만 해당)
        Optional<CouponLike> existingLikeOpt = couponLikeRepository.findActiveLike(couponId, userIdLong);

        boolean isLiked;

        if (existingLikeOpt.isPresent()) {
            // 이미 좋아요 상태면 삭제 (좋아요 취소)
            couponLikeRepository.delete(existingLikeOpt.get());
            isLiked = false;
        } else {
            // 없으면 새로 좋아요 생성
            CouponLike newLike = CouponLike.create(couponId, userId, brandId);
            couponLikeRepository.save(newLike);
            isLiked = true;
        }

        return new CouponLikeResponse(isLiked, Long.valueOf(couponId));
    }


    @Transactional(readOnly = true)
    public List<CouponSummary> getBestCoupons() {
        List<Coupon> topCoupons = couponRepository.findTop3ByLikeCount();
        return topCoupons.stream()
                .map(CouponSummary::from)
                .collect(Collectors.toList());
    }
}
