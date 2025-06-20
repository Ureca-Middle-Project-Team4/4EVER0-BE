package com.team4ever.backend.domain.user.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", nullable = true)
    private Integer planId;

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    private String userId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(name = "attendance_streaks", nullable = false)
    private Integer attendanceStreak;

    @Column(nullable = false)
    private Integer point;

    // User.java에 추가
    public void addPoint(int points) {
        this.point += points;   // 현재 보유 포인트에 새 포인트 추가
    }
}

