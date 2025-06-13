package com.team4ever.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LikedCouponsResponse {
	private List<LikedCouponDto> coupons;
}