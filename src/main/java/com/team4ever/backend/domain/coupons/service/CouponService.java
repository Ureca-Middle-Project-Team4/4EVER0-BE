package com.team4ever.backend.domain.coupons.service;

import com.team4ever.backend.domain.coupons.dto.CouponLikeResponseDto;
import com.team4ever.backend.domain.coupons.dto.CouponSummaryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {

    public CouponLikeResponseDto likeCoupon(Long couponId) {
        // 실제로는 좋아요 등록 로직 수행 (DB insert 등)
        return new CouponLikeResponseDto(true, couponId);
    }

    public List<CouponSummaryDto> getBestCoupons() {
        // 실제로는 DB에서 인기 쿠폰 3개를 가져오는 로직 수행
        return List.of(
                new CouponSummaryDto(1L, "전자책 무제한 구독", "리디셀렉트 구독 시 전자책 무제한 열람 가능", "리디셀렉트", "PERCENT", 100, "2025-06-01", "2025-06-30"),
                new CouponSummaryDto(2L, "파인트 선착순 할인", "베스킨라빈스 파인트 구매 시 4천원 할인", "베스킨라빈스", "FIXED", 4000, "2025-06-01", "2025-06-30")
        );
    }
}
