package com.team4ever.backend.domain.mission.entity;

import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    private int progressCount;

    @Enumerated(EnumType.STRING)
    private MissionStatus status; // INP, COM, REC

    private LocalDateTime createdAt;

    public void increaseProgress() {
        if (this.status == MissionStatus.REC) return;

        this.progressCount += 1;

        if (this.progressCount >= mission.getTargetCount()) {
            this.status = MissionStatus.COM;
        }
    }

    public boolean isCompleted() {
        return this.status == MissionStatus.COM;
    }

    public void receiveReward() {
        if (this.status == MissionStatus.INP) {
            throw new CustomException(ErrorCode.MISSION_NOT_COMPLETED);
        }
        if (this.status == MissionStatus.REC) {
            throw new CustomException(ErrorCode.REWARD_ALREADY_RECEIVED);
        }
        this.status = MissionStatus.REC;
    }
}
