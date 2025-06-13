package com.team4ever.backend.domain.mission.controller;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.dto.UserMissionDto;
import com.team4ever.backend.domain.mission.service.MissionService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
@Tag(name = "Mission", description = "미션 관련 API")
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    @Operation(
            summary = "전체 미션 목록 조회",
            description = "현재 유효한 모든 미션 목록을 조회합니다. (완료일이 오늘 이후인 미션만)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<List<MissionDto>> getAllMissions() {
        List<MissionDto> missions = missionService.getAllMissions();
        return BaseResponse.success(missions);
    }

    @PatchMapping("/{missionId}/progress")
    @Operation(
            summary = "미션 진행도 업데이트",
            description = "특정 사용자의 미션 진행도를 1 증가시킵니다. 목표 달성 시 자동으로 완료 상태로 변경됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "진행도 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<String> updateProgress(
            @Parameter(description = "미션 ID", required = true)
            @PathVariable Long missionId,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        String result = missionService.updateMissionProgress(userId, missionId);
        return BaseResponse.success(result);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "사용자별 미션 진행 상황 조회",
            description = "특정 사용자의 모든 미션 진행 상황을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 미션 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<List<UserMissionDto>> getUserMissions(
            @Parameter(description = "사용자 ID", required = true)
            @PathVariable Long userId) {
        List<UserMissionDto> userMissions = missionService.getUserMissions(userId);
        return BaseResponse.success(userMissions);
    }

    // User 패키지 생성 후 다시 보기
    @PatchMapping("/{missionId}/reward")
    @Operation(
            summary = "미션 보상 수령",
            description = "완료된 미션의 보상을 수령합니다. 포인트가 사용자 계정에 적립됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보상 수령 성공"),
            @ApiResponse(responseCode = "400", description = "미션이 완료되지 않았거나 이미 보상을 수령함"),
            @ApiResponse(responseCode = "404", description = "미션 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<String> receiveReward(
            @Parameter(description = "미션 ID", required = true)
            @PathVariable Long missionId,
            @Parameter(description = "사용자 ID", required = true)
            @RequestParam Long userId) {
        String result = missionService.receiveMissionReward(userId, missionId);
        return BaseResponse.success(result);
    }

}
