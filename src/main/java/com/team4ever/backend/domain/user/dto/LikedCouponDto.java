package com.team4ever.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team4ever.backend.domain.coupon.entity.Coupon;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikedCouponDto {
	private Integer id;
	private String title;
	private String description;

	@JsonProperty("discount_type")
	private String discountType;

	@JsonProperty("discount_value")
	private Integer discountValue;

	@JsonProperty("start_date")
	private LocalDate startDate;

	@JsonProperty("end_date")
	private LocalDate endDate;

	@JsonProperty("brand_name")
	private String brandName;

	@JsonProperty("brand_id")
	private Integer brandId;

	// JPQL 생성자 쿼리용 생성자
	public LikedCouponDto(Integer id, String title, String description,
						  Coupon.DiscountType discountType, Integer discountValue,
						  LocalDate startDate, LocalDate endDate,
						  String brandName, Integer brandId) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.discountType = discountType.name();
		this.discountValue = discountValue;
		this.startDate = startDate;
		this.endDate = endDate;
		this.brandName = brandName;
		this.brandId = brandId;
	}
}
