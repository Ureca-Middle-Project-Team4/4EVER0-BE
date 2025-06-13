package com.team4ever.backend.domain.subscriptions.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '구독 상품 PK'")
	private Integer id;

	@Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '구독 상품 제목'")
	private String title;

	@Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '카테고리'")
	private String category;

	@Column(nullable = false, columnDefinition = "INTEGER COMMENT '가격'")
	private Integer price;

	@Column(columnDefinition = "TEXT COMMENT '이미지 URL'")
	private String imageUrl;
}