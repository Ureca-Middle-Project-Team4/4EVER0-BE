package com.team4ever.backend.domain.benefit.dto;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BenefitResponse {

    private String brand;
    private LocalDate date;

    public static BenefitResponse from(Benefit benefit) {
        return new BenefitResponse(benefit.getBrand().getName(), benefit.getBenefitDate());
    }
}
