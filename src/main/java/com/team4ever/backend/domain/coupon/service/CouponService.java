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

    @Transactional
    public CouponLikeResponse likeCoupon(Integer couponId, Long userId, Integer brandId) {
        CouponLike like = couponLikeRepository.findByCouponIdAndUserId(couponId, userId)
                .orElse(null);

        if (like != null) {
            if (like.isLiked()) {
                throw new CustomException(ErrorCode.COUPON_ALREADY_LIKED);
            }
            like.like();
        } else {
            couponLikeRepository.save(CouponLike.create(couponId, userId.intValue(), brandId));
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
