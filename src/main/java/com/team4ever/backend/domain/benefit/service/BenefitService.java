package com.team4ever.backend.domain.benefit.service;

import com.team4ever.backend.domain.benefit.dto.BenefitDetailResponse;
import com.team4ever.backend.domain.benefit.dto.BenefitPreviewResponse;
import com.team4ever.backend.domain.benefit.entity.Benefit;
import com.team4ever.backend.domain.benefit.repository.BenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitService {

    private final BenefitRepository benefitRepository;

    // 이번달 혜택 전체조회 - 미리보기용 (brand, date, imageUrl만)
    public List<BenefitPreviewResponse> getMonthlyBenefits() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<Benefit> benefits = benefitRepository.findAllByYearAndMonthWithBrand(year, month);

        return benefits.stream()
                .map(BenefitPreviewResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 날짜 혜택 조회 - 상세정보 포함 (brand, date, imageUrl, description, category)
    public List<BenefitDetailResponse> getBenefitsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);

        List<Benefit> benefits = benefitRepository.findAllByBenefitDateWithBrand(localDate);

        return benefits.stream()
                .map(BenefitDetailResponse::from)
                .collect(Collectors.toList());
    }
}