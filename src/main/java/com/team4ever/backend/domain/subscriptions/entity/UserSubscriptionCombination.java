package com.team4ever.backend.domain.subscriptions.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscription_combinations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionCombination {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '사용자 구독 조합 PK'")
	private Integer id;

	@Column(name = "user_id", nullable = false, columnDefinition = "BIGINT COMMENT '사용자 ID'")
	private Long userId;

	@Column(name = "subscription_combination_id", nullable = false, columnDefinition = "INTEGER COMMENT '구독 조합 ID'")
	private Integer subscriptionCombinationId;

	@Column(name = "price", nullable = false, columnDefinition = "INTEGER COMMENT '결제 가격'")
	private Integer price;

	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '구독일'")
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		if (this.createdAt == null) {
			this.createdAt = LocalDateTime.now();
		}
	}
}