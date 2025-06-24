package com.team4ever.backend.domain.user.dto;

import java.util.List;
import com.team4ever.backend.domain.user.dto.CouponInfoResponse;


public record UserCouponListResponse(
        List<CouponInfoResponse> coupons
) {}
