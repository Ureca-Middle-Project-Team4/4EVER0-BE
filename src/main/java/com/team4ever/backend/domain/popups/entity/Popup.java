package com.team4ever.backend.domain.popups.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "popup_stores")
@Getter
@Setter
@NoArgsConstructor
public class Popup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED COMMENT '팝업스토어 PK'")
    private Integer id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255) COMMENT '팝업 이름'")
    private String name;

    @Column(columnDefinition = "TEXT COMMENT '설명'")
    private String description;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '주소'")
    private String address;

    @Column(columnDefinition = "DOUBLE COMMENT '위도'")
    private Double latitude;

    @Column(columnDefinition = "DOUBLE COMMENT '경도'")
    private Double longitude;

    @Column(columnDefinition = "VARCHAR(255) COMMENT '이미지 URL'")
    private String imageUrl;

    @Column(columnDefinition = "INT UNSIGNED COMMENT '작성자 ID'", nullable = false)
    private Integer userId;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0 COMMENT '북마크 여부'")
    private Boolean isBookmarked = false;
}