package com.team4ever.backend.domain.benefit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BenefitApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> BenefitApiResponse<T> ok(String message, T data) {
        return new BenefitApiResponse<>(200, message, data);
    }
}
