package com.team4ever.backend.domain.plan.controller;

import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.service.PlanService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "요금제 API", description = "요금제 조회 관련 API")
public class PlanController {

	private final PlanService planService;

	@Operation(summary = "전체 요금제 조회")
	@GetMapping
	public ResponseEntity<BaseResponse<List<PlanResponse>>> getAllPlans() {
		List<PlanResponse> response = planService.getAllPlans();
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(summary = "요금제 상세 조회")
	@GetMapping("/{planId}")
	public ResponseEntity<BaseResponse<PlanResponse>> getPlanDetail(
			@PathVariable Integer planId
	) {
		PlanResponse response = planService.getPlanDetail(planId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}