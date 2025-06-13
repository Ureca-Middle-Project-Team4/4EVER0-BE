package com.team4ever.backend.domain.plan.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "INTEGER COMMENT '요금제 PK'")
	private Integer id;

	@Column(nullable = false, columnDefinition = "VARCHAR(100) COMMENT '요금제 명'")
	private String name;

	@Column(nullable = false, columnDefinition = "VARCHAR(20) COMMENT '가격'")
	private String price;

	@Column(columnDefinition = "TEXT COMMENT '설명'")
	private String description;

	@Column(columnDefinition = "VARCHAR(50) COMMENT '데이터량'")
	private String data;

	@Column(columnDefinition = "VARCHAR(50) COMMENT '속도'")
	private String speed;

	@Column(columnDefinition = "VARCHAR(50) COMMENT 'SMS'")
	private String sms;

	@Column(columnDefinition = "VARCHAR(50) COMMENT '음성통화'")
	private String voice;

	@Column(name = "share_data", columnDefinition = "VARCHAR(50) COMMENT '공유 데이터'")
	private String shareData;

	@Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE COMMENT '활성화 여부'")
	private Boolean isActive = true;
}
