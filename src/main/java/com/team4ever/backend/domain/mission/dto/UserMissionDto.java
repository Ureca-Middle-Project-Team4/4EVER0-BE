package com.team4ever.backend.domain.mission.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team4ever.backend.domain.mission.entity.MissionStatus;
import com.team4ever.backend.domain.mission.entity.MissionType;
import com.team4ever.backend.domain.mission.entity.UserMission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserMissionDto {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("mission_id")
    private Long missionId;

    @JsonProperty("mission_name")
    private String missionName;

    @JsonProperty("progress_count")
    private int progressCount;

    @JsonProperty("target_count")
    private int targetCount;

    private MissionStatus status;

    @JsonProperty("reward_point")
    private int rewardPoint;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("is_completed")
    private boolean isCompleted;

    @JsonProperty("type")
    private MissionType type;

    public static UserMissionDto from(UserMission entity) {
        return new UserMissionDto(
                entity.getId(),
                entity.getUserId(),
                entity.getMission().getId(),
                entity.getMission().getName(),
                entity.getProgressCount(),
                entity.getMission().getTargetCount(),
                entity.getStatus(),
                entity.getMission().getRewardPoint(),
                entity.getCreatedAt(),
                entity.isCompleted(),
                entity.getMission().getType()
        );
    }
}