package com.team4ever.backend.domain.coupon.controller;

import com.team4ever.backend.domain.common.brand.Brand;
import com.team4ever.backend.domain.common.brand.BrandRepository;
import com.team4ever.backend.domain.coupon.dto.*;
import com.team4ever.backend.domain.coupon.service.CouponService;
import com.team4ever.backend.domain.maps.dto.PlaceSearchRequest;
import com.team4ever.backend.domain.maps.dto.PlaceSearchResponse;
import com.team4ever.backend.domain.maps.service.PlaceService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "쿠폰 API", description = "쿠폰 조회, 발급, 사용 관련 API")
public class CouponController {

    private final CouponService couponService;
    private final BrandRepository brandRepository;
    private final PlaceService placeService;
    private final UserRepository userRepository;

    @Operation(
            summary = "전체 쿠폰 조회",
            description = """
            모든 사용자가 이용할 수 있는 쿠폰 목록을 조회합니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "쿠폰 목록 조회 성공",
                          "data": [
                            {
                              "id": 1,
                              "title": "스타벅스 10% 할인 쿠폰",
                              "description": "모든 음료 10% 할인",
                              "discountType": "PERCENTAGE",
                              "discountValue": 10,
                              "validUntil": "2025-12-31",
                              "brand": {
                                "id": 1,
                                "name": "스타벅스",
                                "imageUrl": "https://example.com/starbucks.png"
                              }
                            }
                          ]
                        }
                        """
                            )
                    )
            )
    })
    @GetMapping
    public BaseResponse<List<CouponResponse>> getAllCoupons() {
        log.info("전체 쿠폰 목록 조회 요청");

        // userId 없이 전체 쿠폰만 반환
        List<CouponResponse> coupons = couponService.getAllCoupons(null);

        log.info("쿠폰 목록 조회 완료 - 쿠폰 수: {}", coupons.size());
        return BaseResponse.success(coupons);
    }

    @Operation(
            summary = "특정 쿠폰 발급 요청",
            description = """
            특정 쿠폰을 현재 로그인한 사용자에게 발급합니다.
            
            **발급 조건:**
            - 로그인된 사용자만 가능
            - 쿠폰 발급 가능 수량 확인
            - 중복 발급 방지
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "쿠폰 발급이 완료되었습니다.",
                          "data": {
                            "couponId": 1,
                            "title": "스타벅스 10% 할인 쿠폰",
                            "claimedAt": "2025-06-15T18:30:00",
                            "validUntil": "2025-12-31T23:59:59",
                            "isUsed": false
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "쿠폰 발급 불가 (이미 발급받음, 수량 초과 등)"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "쿠폰을 찾을 수 없음")
    })
    @SecurityRequirement(name = "cookieAuth")
    @PostMapping("/{couponId}/claim")
    public BaseResponse<CouponClaimResponse> claimCoupon(
            @Parameter(description = "발급받을 쿠폰 ID", required = true, example = "1")
            @PathVariable Integer couponId
    ) {
        log.info("쿠폰 발급 요청 - couponId: {}", couponId);

        Long userId = getCurrentUserIdAsLong();
        CouponClaimResponse response = couponService.claimCoupon(userId, couponId);

        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", userId, couponId);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "특정 쿠폰 사용 처리",
            description = """
            보유한 쿠폰을 사용 처리합니다.
            
            **사용 조건:**
            - 로그인된 사용자만 가능
            - 보유한 쿠폰만 사용 가능
            - 미사용 상태인 쿠폰만 사용 가능
            - 유효기간 내 쿠폰만 사용 가능
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "쿠폰 사용 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "쿠폰이 성공적으로 사용되었습니다.",
                          "data": {
                            "couponId": 1,
                            "title": "스타벅스 10% 할인 쿠폰",
                            "usedAt": "2025-06-15T18:30:00",
                            "discountAmount": 500,
                            "originalPrice": 5000,
                            "finalPrice": 4500
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "쿠폰 사용 불가 (이미 사용됨, 만료됨 등)"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "404", description = "보유한 쿠폰을 찾을 수 없음")
    })


    @SecurityRequirement(name = "cookieAuth")
    @PatchMapping("/{couponId}/use")
    public BaseResponse<CouponUseResponse> useCoupon(
            @Parameter(description = "사용할 쿠폰 ID", required = true, example = "1")
            @PathVariable Integer couponId
    ) {
        log.info("쿠폰 사용 요청 - couponId: {}", couponId);

        Long userId = getCurrentUserIdAsLong();
        CouponUseResponse response = couponService.useCoupon(userId, couponId);

        log.info("쿠폰 사용 완료 - userId: {}, couponId: {}", userId, couponId);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "쿠폰 좋아요/취소",
            description = "사용자가 특정 쿠폰에 좋아요를 누르거나 취소합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "좋아요 상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "success": true,
                                  "message": "성공",
                                  "data": {
                                    "couponId": 1,
                                    "liked": true
                                  }
                                }
                                """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "cookieAuth")
    @PostMapping("/{couponId}/like")
    public BaseResponse<CouponLikeResponse> likeCoupon(
            @PathVariable Integer couponId
    ) {
        Long userId = getCurrentUserIdAsLong();
        CouponLikeResponse response = couponService.likeCoupon(couponId, userId);
        return BaseResponse.success(response);
    }



    @Operation(
            summary = "인기 쿠폰 TOP 3 조회",
            description = """
        좋아요 수 기준으로 가장 인기 있는 쿠폰 3개를 조회합니다.
        
        - 모든 사용자에게 공개된 API
        - 좋아요 수가 많은 순으로 정렬됨
        - 로그인 불필요
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "인기 쿠폰 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "success": true,
                                  "message": "인기 쿠폰 조회 성공",
                                  "data": [
                                    {
                                      "couponId": 5,
                                      "title": "배스킨라빈스 1+1 쿠폰",
                                      "brandName": "배스킨라빈스",
                                      "brandId": 2,
                                      "discountType": "PERCENTAGE",
                                      "discountValue": 50,
                                      "startDate": "2025-06-01",
                                      "endDate": "2025-12-31"
                                    }
                                  ]
                                }
                                """
                            )
                    )
            )
    })


    @GetMapping("/best")
    public BaseResponse<List<CouponSummary>> getBestCoupons() {
        log.info("인기 쿠폰 TOP 3 조회 요청");
        List<CouponSummary> best = couponService.getBestCoupons();
        return BaseResponse.success(best);
    }


    /**
     * SecurityContext에서 현재 사용자 ID 추출
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("인증되지 않은 사용자의 쿠폰 API 접근 시도");
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        String userId = (String) authentication.getPrincipal();
        log.debug("현재 사용자 ID: {}", userId);
        return userId;
    }

    /**
     * JWT에서 추출한 User.userId(String)로 User 엔티티를 조회하여 PK(Long) 반환
     */
    private Long getCurrentUserIdAsLong() {
        try {
            String userIdStr = getCurrentUserId();

            // User.userId(String)로 User 엔티티 조회
            User user = userRepository.findByUserId(userIdStr)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIdStr));

            Long userPkId = user.getId();
            log.debug("JWT userId: {} -> User PK: {}", userIdStr, userPkId);

            return userPkId;

        } catch (Exception e) {
            log.error("사용자 ID 변환 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("사용자 정보를 조회할 수 없습니다.");
        }
    }

    @Operation(
            summary = "근처 쿠폰 사용 가능 매장 조회",
            description = """
        입력한 위도, 경도 주변 반경 500m 내에서
        지정한 브랜드 ID 리스트에 해당하는 쿠폰 사용 가능 매장을 조회합니다.

        - brand_id는 여러 개 전달 가능 (예: brand_id=1&brand_id=2)
        - 각 brand_id에 해당하는 브랜드명으로 매장 검색 수행
        - 각 브랜드마다 최대 10개까지만 반환 가능
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "근처 쿠폰 매장 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "success": true,
                  "message": "근처 쿠폰 매장 조회 성공",
                  "data": {
                    "places": [
                      {
                        "name": "배스킨라빈스 선릉역점",
                        "lat": 37.5053571,
                        "lng": 127.0472785,
                        "address": "대한민국 서울특별시 강남구 역삼동 696-5"
                      },
                      {
                        "name": "올리브영 강남점",
                        "lat": 37.498,
                        "lng": 127.027,
                        "address": "대한민국 서울특별시 강남구 신사동 ..."
                      }
                    ]
                  }
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 brand_id 또는 요청 파라미터 오류",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/nearby")
    public BaseResponse<PlaceSearchResponse> getNearbyCoupons(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam List<Integer> brand_id) throws JSONException {

        // brand_id 리스트 순회하며 브랜드명 조회 및 요청 처리
        List<String> brandNames = new ArrayList<>();
        for (Integer id : brand_id) {
            Brand brand = brandRepository.findBrandId(id);
            String name = brand.getName();
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("브랜드를 찾을 수 없습니다. id=" + id);
            }
            brandNames.add(name);
        }

        // PlaceSearchRequest에 brandNames 리스트 세팅
        PlaceSearchRequest req = new PlaceSearchRequest();
        req.setTextQueryList(brandNames);
        req.setLatitude(lat);
        req.setLongitude(lng);
        req.setRadius(500.0);
        req.setPageSize(10);

        PlaceSearchResponse result = placeService.search(req);

        return BaseResponse.success(result);
    }
}


