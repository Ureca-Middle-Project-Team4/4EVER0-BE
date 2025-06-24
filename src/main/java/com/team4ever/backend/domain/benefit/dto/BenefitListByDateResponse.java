package com.team4ever.backend.domain.benefit.dto;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(staticName = "of")
public class BenefitListByDateResponse {
    private LocalDate date;
    private List<BrandDto> benefits;

    @Getter
    @AllArgsConstructor(staticName = "of")
    public static class BrandDto {
        private String brand;
    }

    public static BenefitListByDateResponse from(LocalDate date, List<Benefit> benefits) {
        List<BrandDto> brandList = benefits.stream()
                .map(b -> BrandDto.of(b.getBrand().getName()))
                .collect(Collectors.toList());

        return BenefitListByDateResponse.of(date, brandList);
    }
}
