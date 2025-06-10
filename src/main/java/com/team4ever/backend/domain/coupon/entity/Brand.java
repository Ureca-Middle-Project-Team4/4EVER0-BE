package com.team4ever.backend.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brands")
@Getter
@NoArgsConstructor
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private String imageUrl;
	private String description;

	// 관리용 타임스탬프
	@Column(nullable = false, updatable = false)
	private java.time.LocalDateTime createdAt = java.time.LocalDateTime.now();

	@Column(nullable = false)
	private java.time.LocalDateTime updatedAt = java.time.LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = java.time.LocalDateTime.now();
	}
}
