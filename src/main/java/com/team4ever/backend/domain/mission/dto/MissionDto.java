package com.team4ever.backend.domain.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team4ever.backend.domain.mission.entity.Mission;
import com.team4ever.backend.domain.mission.entity.MissionType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MissionDto {
    private Long id;
    private String name;
    private String description;
    private MissionType type;

    @JsonProperty("target_count")
    private int targetCount;

    @JsonProperty("reward_point")
    private int rewardPoint;

    @JsonProperty("completed_at")
    private LocalDate completedAt;

    @JsonProperty("image_url")
    private String imageUrl;

    public static MissionDto from(Mission entity) {
        return new MissionDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.getTargetCount(),
                entity.getRewardPoint(),
                entity.getCompletedAt(),
                entity.getImageUrl()
        );
    }
}