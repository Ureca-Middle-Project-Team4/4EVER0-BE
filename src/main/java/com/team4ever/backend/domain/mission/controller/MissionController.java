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
}
