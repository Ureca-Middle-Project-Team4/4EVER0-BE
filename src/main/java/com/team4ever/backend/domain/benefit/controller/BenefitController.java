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
import com.team4ever.backend.domain.benefit.dto.BenefitListByDateResponse;
import com.team4ever.backend.domain.benefit.dto.BenefitListByDateResponse;
import org.springframework.web.bind.annotation.PathVariable;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uplus-benefits")

public class BenefitController {

    private final BenefitService benefitService;


    @Operation(summary = "이번달 혜택 전체 조회")
    @GetMapping()
    public BaseResponse<List<BenefitResponse>> getMonthlyBenefits() {
        return BaseResponse.success(benefitService.getMonthlyBenefits());
    }

    @Operation(summary = "특정 날짜 혜택 조회", description = "YYYY-MM-DD 형식의 날짜로 혜택을 조회합니다")
    @GetMapping("/{date}")
    public BaseResponse<List<BenefitResponse>> getBenefitsByDate(@PathVariable String date) {
        return BaseResponse.success(benefitService.getBenefitsByDate(date));

    }

}
