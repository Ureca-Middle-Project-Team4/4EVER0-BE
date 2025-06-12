package com.team4ever.backend.domain.common.couponlike;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "coupon_likes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponLike {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "coupon_id", nullable = false)
	private Integer couponId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "brand_id", nullable = false)
	private Integer brandId;

	@Column(name = "is_liked", nullable = false)
	private Boolean isLiked = false;
}