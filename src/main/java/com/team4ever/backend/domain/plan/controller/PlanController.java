package com.team4ever.backend.domain.plan.controller;

import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.service.PlanService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "요금제 API", description = "요금제 조회 관련 API (인증 불필요)")
public class PlanController {

	private final PlanService planService;

	@Operation(
			summary = "전체 요금제 조회",
			description = "활성화된 모든 요금제 목록을 조회합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 목록 조회 성공",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			)
	})
	@GetMapping
	public ResponseEntity<BaseResponse<List<PlanResponse>>> getAllPlans() {
		List<PlanResponse> response = planService.getAllPlans();
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
			summary = "요금제 상세 조회",
			description = "특정 요금제의 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "요금제 상세 조회 성공",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "404",
					description = "요금제를 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			),
			@ApiResponse(
					responseCode = "500",
					description = "서버 내부 오류",
					content = @Content(schema = @Schema(implementation = BaseResponse.class))
			)
	})
	@GetMapping("/{planId}")
	public ResponseEntity<BaseResponse<PlanResponse>> getPlanDetail(
			@Parameter(
					description = "조회할 요금제 ID",
					required = true,
					example = "1"
			)
			@PathVariable Integer planId
	) {
		PlanResponse response = planService.getPlanDetail(planId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}