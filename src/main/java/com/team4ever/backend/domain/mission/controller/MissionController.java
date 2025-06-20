package com.team4ever.backend.domain.mission.controller;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.dto.UserMissionDto;
import com.team4ever.backend.domain.mission.service.MissionService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
@Tag(name = "미션 API", description = "미션 조회 및 진행, 보상 수령 API (인증 필요)")
public class MissionController {

    private final MissionService missionService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "전체 미션 목록 조회", description = "현재 유효한 모든 미션 목록을 조회합니다. (완료일이 오늘 이후인 미션만)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<List<MissionDto>> getAllMissions() {
        List<MissionDto> missions = missionService.getAllMissions();
        return BaseResponse.success(missions);
    }

    @PatchMapping("/{missionId}/progress")
    @Operation(summary = "미션 진행도 업데이트", description = "JWT 기반 사용자 인증 후 미션 진행도를 1 증가시킵니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "진행도 업데이트 성공"),
            @ApiResponse(responseCode = "404", description = "미션을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<String> updateProgress(@PathVariable Long missionId) {
        Long userPk = getCurrentUserPk();
        String result = missionService.updateMissionProgress(userPk, missionId);
        return BaseResponse.success(result);
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 미션 진행 상황 조회",
            description = """
            JWT 인증된 사용자의 **진행 중**, **완료**, **보상 수령 여부** 등 미션 상태를 조회합니다.

            - 미션 상태: INP(진행 중), COM(완료), REC(보상 수령)
            - 유저마다 미션은 자동 생성되어 매핑됩니다.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 미션 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "success": true,
                                      "message": "요청 성공",
                                      "data": [
                                        {
                                          "missionId": 1,
                                          "title": "친구 초대하기",
                                          "progressCount": 1,
                                          "status": "INP"
                                        }
                                      ]
                                    }
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<List<UserMissionDto>> getMyMissions() {
        Long userPk = getCurrentUserPk();
        List<UserMissionDto> userMissions = missionService.getUserMissions(userPk);
        return BaseResponse.success(userMissions);
    }

    @PatchMapping("/{missionId}/reward")
    @Operation(
            summary = "미션 보상 수령",
            description = """
            완료 상태(COM)인 미션에 대해 JWT 사용자 기준으로 보상을 수령합니다.

            - 보상은 1회만 수령 가능
            - 이미 수령된 경우 400 에러 반환
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "보상 수령 성공"),
            @ApiResponse(responseCode = "400", description = "미션이 완료되지 않았거나 이미 보상을 수령함"),
            @ApiResponse(responseCode = "404", description = "미션 또는 사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public BaseResponse<String> receiveReward(@PathVariable Long missionId) {
        Long userPk = getCurrentUserPk();
        String result = missionService.receiveMissionReward(userPk, missionId);
        return BaseResponse.success(result);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        return (String) authentication.getPrincipal();
    }

    private Long getCurrentUserPk() {
        String userId = getCurrentUserId();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }
}
