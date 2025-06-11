package com.team4ever.backend.domain.common.brand;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "brands")
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '브랜드 PK'")
	private Integer id;

	@Column(nullable = false, columnDefinition = "VARCHAR(50) COMMENT '브랜드 이름'")
	private String name;

	@Column(name = "image_url", nullable = false, columnDefinition = "TEXT COMMENT '이미지 URL'")
	private String imageUrl;

	@Column(columnDefinition = "TEXT COMMENT '설명'")
	private String description;

	@Column(columnDefinition = "VARCHAR(100) COMMENT '카테고리'")
	private String category;
}
