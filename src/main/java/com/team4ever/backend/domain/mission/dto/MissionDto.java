package com.team4ever.backend.domain.mission.dto;

import com.team4ever.backend.domain.mission.entity.Mission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MissionDto {
    private Long id;
    private String name;
    private String description;
    private String type;
    private int targetCount;
    private int rewardPoint;
    private LocalDate completedAt;
    private String imageUrl;

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