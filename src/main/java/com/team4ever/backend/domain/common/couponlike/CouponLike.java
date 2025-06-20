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

	@Builder.Default
	@Column(name = "is_liked", nullable = false)
	private Boolean isLiked = false;

	public boolean isLiked() {
		return Boolean.TRUE.equals(this.isLiked);
	}

	public void toggle() {
		this.isLiked = !this.isLiked;
	}

	public static CouponLike create(Integer couponId, Integer userId, Integer brandId) {
		if (brandId == null) {
			throw new IllegalArgumentException("brandId must not be null when creating a CouponLike.");
		}

		return CouponLike.builder()
				.couponId(couponId)
				.userId(Long.valueOf(userId))
				.brandId(brandId)
				.isLiked(true)
				.build();
	}

}
