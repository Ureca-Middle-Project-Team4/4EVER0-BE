package com.team4ever.backend.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_likes")
@Getter
@NoArgsConstructor
public class CouponLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer couponId;
    private Integer userId;
    private Integer brandId;
    private boolean isLiked;

    public static CouponLike create(Integer couponId, Integer userId, Integer brandId) {
        CouponLike cl = new CouponLike();
        cl.couponId = couponId;
        cl.userId = userId;
        cl.brandId = brandId;
        cl.isLiked = true;
        return cl;
    }

    public void like() {
        this.isLiked = true;
    }

    public void unlike() {
        this.isLiked = false;
    }
}

