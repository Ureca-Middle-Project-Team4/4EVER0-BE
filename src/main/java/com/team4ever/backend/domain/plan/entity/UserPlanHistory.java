package com.team4ever.backend.domain.plan.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_plan_histories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPlanHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '사용자 요금제 이력 PK'")
	private Integer id;

	@Column(name = "user_id", nullable = false, columnDefinition = "BIGINT COMMENT '사용자 ID'")
	private Long userId;

	@Column(name = "plan_id", nullable = false, columnDefinition = "INTEGER COMMENT '요금제 ID'")
	private Integer planId;

	@Column(name = "action_type", nullable = false, columnDefinition = "VARCHAR(20) COMMENT '액션 타입 (CHANGE, CANCEL)'")
	@Enumerated(EnumType.STRING)
	private ActionType actionType;

	@Column(name = "changed_at", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '변경일시'")
	private LocalDateTime changedAt;

	@PrePersist
	protected void onCreate() {
		if (this.changedAt == null) {
			this.changedAt = LocalDateTime.now();
		}
	}

	public enum ActionType {
		CHANGE, CANCEL
	}
}
