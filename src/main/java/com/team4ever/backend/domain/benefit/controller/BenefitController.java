package com.team4ever.backend.domain.benefit.controller;

import com.team4ever.backend.domain.benefit.dto.BenefitResponse;
import com.team4ever.backend.domain.benefit.service.BenefitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;
import com.team4ever.backend.domain.benefit.dto.BenefitApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uplus-benefits")
public class BenefitController {

    private final BenefitService benefitService;

    @Operation(summary = "이번 달 전체 혜택 조회")
    @GetMapping
    public BenefitApiResponse<List<BenefitResponse>> getAllBenefits() {
        return BenefitApiResponse.ok("요청이 성공적으로 처리되었습니다.", benefitService.getAllBenefits());
    }

    @Operation(summary = "특정 날짜 혜택 조회")
    @GetMapping(params = "date")
    public BenefitApiResponse<List<BenefitResponse>> getBenefitsByDate(@RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return BenefitApiResponse.ok("요청이 성공적으로 처리되었습니다.", benefitService.getBenefitsByDate(parsedDate));
    }
}
