package com.team4ever.backend.domain.benefit.service;

import com.team4ever.backend.domain.benefit.dto.BenefitResponse;
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

    public List<BenefitResponse> getMonthlyBenefits() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        List<Benefit> benefits = benefitRepository.findAllByYearAndMonth(year, month);

        return benefits.stream()
                .map(BenefitResponse::from)
                .collect(Collectors.toList());
    }

    public List<BenefitResponse> getBenefitsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Benefit> benefits = benefitRepository.findAllByBenefitDate(localDate);

        return benefits.stream()
                .map(BenefitResponse::from)
                .collect(Collectors.toList());
    }
}