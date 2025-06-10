package com.team4ever.backend.domain.mission.dto;

import com.team4ever.backend.domain.mission.entity.Mission;

import java.time.LocalDate;

public record MissionDto(
        Long id,
        String name,
        String description,
        String type,
        int targetCount,
        int rewardPoint,
        LocalDate completedAt,
        String imageUrl
) {
    public static MissionDto from(Mission mission) {
        return new MissionDto(
                mission.getId(),
                mission.getName(),
                mission.getDescription(),
                mission.getType().name(),
                mission.getTargetCount(),
                mission.getRewardPoint(),
                mission.getCompletedAt(),
                mission.getImageUrl()
        );
    }
}
