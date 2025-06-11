package com.team4ever.backend.domain.subscriptions.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "subscription_combinations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionCombination {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '구독 조합 PK'")
	private Integer id;

	@Column(name = "subscription_id", nullable = false, columnDefinition = "INTEGER COMMENT '구독 상품 ID'")
	@JsonProperty("subscription_id")
	private Integer subscriptionId;

	@Column(name = "brand_id", nullable = false, columnDefinition = "INTEGER COMMENT '브랜드 ID'")
	@JsonProperty("brand_id")
	private Integer brandId;

	@Column(name = "user_id", nullable = false, columnDefinition = "INTEGER COMMENT '사용자 ID'")
	@JsonProperty("user_id")
	private Integer userId;

	@Column(name = "price", nullable = false, columnDefinition = "INTEGER COMMENT '총 가격'")
	private Integer price;
}