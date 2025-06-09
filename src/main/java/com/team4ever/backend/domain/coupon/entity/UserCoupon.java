package com.team4ever.backend.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.data.annotation.Id;
import com.team4ever.backend.domain.coupon.entity.Coupon;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    private Boolean isUsed = false;

    public void markAsUsed() {
        this.isUsed = true;
    }
}
