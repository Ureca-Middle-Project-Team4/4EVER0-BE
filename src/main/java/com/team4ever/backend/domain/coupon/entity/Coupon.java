package com.team4ever.backend.domain.coupon.entity;

import com.team4ever.backend.domain.common.brand.Brand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED COMMENT '쿠폰 PK'")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Integer discountValue;

    private LocalDate startDate;
    private LocalDate endDate;

    private int likes = 0;

    public int getLikes() {
        return likes;
    }

    public void increaseLikes() {
        this.likes += 1;
    }

    public enum DiscountType {
        PERCENT, FIXED
    }
}
