package com.team4ever.backend.domain.mission.controller;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.service.MissionService;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @GetMapping
    public BaseResponse<List<MissionDto>> getAllMissions() {
        List<MissionDto> missions = missionService.getAllMissions();
        return BaseResponse.success(missions);
    }

    @PatchMapping("/{missionId}/progress")
    public BaseResponse<String> updateProgress(@PathVariable Long missionId,
                                               @RequestParam Integer userId) {
        String result = missionService.updateMissionProgress(userId, missionId);
        return BaseResponse.success(result);
    }

    // User 패키지 생성 후 다시 보기
//    @PatchMapping("/{missionId}/reward")
//    public BaseResponse<String> receiveReward(@PathVariable Long missionId,
//                                              @RequestParam Integer userId) {
//        String result = missionService.receiveMissionReward(userId, missionId);
//        return BaseResponse.success(result);
//    }


}
