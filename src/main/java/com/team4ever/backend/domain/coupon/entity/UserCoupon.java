package com.team4ever.backend.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@Setter
@NoArgsConstructor
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED COMMENT '유저쿠폰 PK'")
    private Integer id;

    @Column(columnDefinition = "INT UNSIGNED COMMENT '유저 FK'")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "coupon_id",
            nullable = false
    )
    private Coupon coupon;


    private Boolean isUsed = false;

    public static UserCoupon of(Integer userId, Coupon coupon) {
        UserCoupon uc = new UserCoupon();
        uc.userId = userId;
        uc.coupon = coupon;
        uc.isUsed = false;
        return uc;
    }

    public void markAsUsed() {
        this.isUsed = true;
    }
}
