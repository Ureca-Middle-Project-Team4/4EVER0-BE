package com.team4ever.backend.domain.benefit.controller;

import com.team4ever.backend.domain.benefit.dto.BenefitResponse;
import com.team4ever.backend.domain.benefit.service.BenefitService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uplus-benefits")

public class BenefitController {

    private final BenefitService benefitService;

    @GetMapping
    public BaseResponse<List<BenefitResponse>> getAllBenefits() {
        return BaseResponse.success(benefitService.getAllBenefits());
    }

    @Operation(summary = "특정 날짜 혜택 조회", description = "YYYY-MM-DD")
    @GetMapping("/by-date")
    public BaseResponse<List<BenefitResponse>> getBenefitsBySpecificDate(
            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-06-12")
            @RequestParam String date
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        return BaseResponse.success(benefitService.getBenefitsByDate(parsedDate));
    }

}
