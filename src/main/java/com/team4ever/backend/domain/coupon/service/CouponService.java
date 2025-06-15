package com.team4ever.backend.domain.coupon.service;

import com.team4ever.backend.domain.common.couponlike.CouponLike;
import com.team4ever.backend.domain.common.couponlike.CouponLikeRepository;
import com.team4ever.backend.domain.coupon.dto.*;
import com.team4ever.backend.domain.coupon.entity.Coupon;
import com.team4ever.backend.domain.coupon.entity.UserCoupon;
import com.team4ever.backend.domain.coupon.repository.CouponRepository;
import com.team4ever.backend.domain.coupon.repository.UserCouponRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponLikeRepository couponLikeRepository;

    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons(Long userId) {
        LocalDate today = LocalDate.now();
        return couponRepository.findAllValid(today).stream()
                .map(c -> {
                    boolean used = userCouponRepository
                            .findByUserIdAndCouponId(userId, c.getId())
                            .map(UserCoupon::getIsUsed)
                            .orElse(false);
                    return CouponResponse.from(c, used);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponClaimResponse claimCoupon(Long userId, Integer couponId) {
        var coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_FOUND));

        LocalDate today = LocalDate.now();
        if (today.isBefore(coupon.getStartDate()) || today.isAfter(coupon.getEndDate())) {
            throw new CustomException(ErrorCode.COUPON_EXPIRED);
        }
        if (userCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new CustomException(ErrorCode.COUPON_ALREADY_CLAIMED);
        }

        UserCoupon uc = UserCoupon.of(userId, coupon);
        userCouponRepository.save(uc);
        return CouponClaimResponse.from(uc);
    }

    @Transactional
    public CouponUseResponse useCoupon(Long userId, Integer couponId) {
        UserCoupon uc = userCouponRepository.findByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUPON_NOT_CLAIMED));
        if (uc.getIsUsed()) {
            throw new CustomException(ErrorCode.COUPON_ALREADY_USED);
        }
        uc.markAsUsed();
        return CouponUseResponse.from(uc);
    }

//    @Transactional
//    public CouponLikeResponse likeCoupon(Integer couponId, Long userId, Integer brandId) {
//        CouponLike like = couponLikeRepository.findByCouponIdAndUserId(couponId, userId)
//                .orElse(null);
//
//        boolean isLiked;
//
//        if (like != null) {
//            like.toggle();
//            couponLikeRepository.save(like); // save() 호출
//            isLiked = like.isLiked();
//        } else {
//            couponLikeRepository.save(CouponLike.create(couponId, userId.intValue(), brandId));
//            isLiked = true;
//        }
//
//        return new CouponLikeResponse(isLiked, Long.valueOf(couponId));
//    }

@Transactional
public CouponLikeResponse likeCoupon(Integer couponId, Long userId, Integer brandId) {
    System.out.println("👉 likeCoupon 실행됨: couponId=" + couponId + ", userId=" + userId + ", brandId=" + brandId);

    CouponLike like = couponLikeRepository.findByCouponIdAndUserId(couponId, userId)
            .orElse(null);

    System.out.println("👉 기존 좋아요 여부: " + (like != null ? like.isLiked() : "없음"));

    boolean isLiked;

    if (like != null) {
        like.toggle();  // 여기가 NPE 가능성 있음
        couponLikeRepository.save(like);
        isLiked = like.isLiked();
        System.out.println("✅ 좋아요 토글 후 상태: " + isLiked);
    } else {
        if (brandId == null) {
            System.out.println("❌ brandId가 null이야! create()에서 터질 수 있음");
        }

        CouponLike newLike = CouponLike.create(couponId, userId.intValue(), brandId);  // ← 여기도 NPE 가능성
        couponLikeRepository.save(newLike);
        isLiked = true;
        System.out.println("✅ 좋아요 신규 등록 완료");
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
