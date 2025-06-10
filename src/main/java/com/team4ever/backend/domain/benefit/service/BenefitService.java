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

    public List<BenefitResponse> getAllBenefits() {
        return benefitRepository.findAll().stream()
                .map(BenefitResponse::from)
                .collect(Collectors.toList());
    }

    public List<BenefitResponse> getBenefitsByDate(LocalDate date) {
        return benefitRepository.findAllByDate(date).stream()
                .map(BenefitResponse::from)
                .collect(Collectors.toList());
    }
}
