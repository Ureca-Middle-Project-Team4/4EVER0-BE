package com.team4ever.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private Integer planId;
    private String userId;
    private String email;
    private String phoneNumber;
    private String name;
    private LocalDate birth;
    private Integer attendanceStreak;
    private Integer point;
}