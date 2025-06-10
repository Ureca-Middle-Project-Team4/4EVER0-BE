package com.team4ever.backend.domain.mission.service;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;

    @Transactional(readOnly = true)
    public List<MissionDto> getAllMissions() {
        return missionRepository.findAll().stream()
                .map(MissionDto::from)
                .toList();
    }
}
