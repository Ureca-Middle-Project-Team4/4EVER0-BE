package com.team4ever.backend.domain.benefit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team4ever.backend.domain.benefit.entity.Benefit;
import lombok.*;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BenefitPreviewResponse {

    private String brand;
    private LocalDate date;
    @JsonProperty("image_url")
    private String imageUrl;

    public static BenefitPreviewResponse from(Benefit benefit) {
        return new BenefitPreviewResponse(
                benefit.getBrand().getName(),
                benefit.getBenefitDate(),
                benefit.getBrand().getImageUrl()
        );
    }
}