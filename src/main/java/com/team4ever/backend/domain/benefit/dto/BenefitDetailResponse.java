package com.team4ever.backend.domain.benefit.dto;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BenefitDetailResponse {

    private String brand;
    private LocalDate date;
    private String imageUrl;
    private String description;
    private String category;

    public static BenefitDetailResponse from(Benefit benefit) {
        return new BenefitDetailResponse(
                benefit.getBrand().getName(),
                benefit.getBenefitDate(),
                benefit.getBrand().getImageUrl(),
                benefit.getBrand().getDescription(),
                benefit.getBrand().getCategory()
        );
    }
}