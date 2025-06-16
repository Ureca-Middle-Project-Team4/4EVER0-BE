package com.team4ever.backend.domain.coupon.controller;

import com.team4ever.backend.domain.common.brand.Brand;
import com.team4ever.backend.domain.common.brand.BrandRepository;
import com.team4ever.backend.domain.coupon.dto.CouponClaimResponse;
import com.team4ever.backend.domain.coupon.dto.CouponResponse;
import com.team4ever.backend.domain.coupon.dto.CouponUseResponse;
import com.team4ever.backend.domain.coupon.repository.CouponRepository;
import com.team4ever.backend.domain.coupon.service.CouponService;
import com.team4ever.backend.domain.maps.dto.PlaceSearchRequest;
import com.team4ever.backend.domain.maps.dto.PlaceSearchResponse;
import com.team4ever.backend.domain.maps.service.PlaceService;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "쿠폰 API", description = "할인 쿠폰 사용 관련 서비스")
public class CouponController {

    private final CouponService couponService;
    private final BrandRepository brandRepository;
    private final PlaceService placeService;

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

    @Operation(summary = "근처 쿠폰 사용 가능 매장 조회")
    @GetMapping("/nearby")
    public BaseResponse<PlaceSearchResponse> getNearbyCoupons(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam Integer brand_id) throws JSONException {

        // 1. brand_id로 브랜드명 조회 (예: BrandRepository 활용)
        Brand brand = brandRepository.findBrandId(brand_id);
        String brandName = brand.getName();
        if (brandName == null || brandName.isBlank()) {
            throw new IllegalArgumentException("브랜드를 찾을 수 없습니다.");
        }

        // 2. GooglePlaceService용 Request 생성
        PlaceSearchRequest req = new PlaceSearchRequest();
        req.setTextQuery(brandName);
        req.setLatitude(lat);
        req.setLongitude(lng);
        req.setRadius(500.0); // 500m 반경 등
        req.setPageSize(10); // 결과 개수 제한
        req.setOpenNow(false); // 영업중만 필터는 필요에 따라

        // 3. Service 호출
        PlaceSearchResponse result = placeService.search(req);

        return BaseResponse.success(result);
    }



}
