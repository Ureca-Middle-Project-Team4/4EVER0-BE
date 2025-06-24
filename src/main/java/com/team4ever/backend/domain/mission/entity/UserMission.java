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
@Table(name = "user_missions")  // 테이블명 명시
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")  // 컬럼명 명시
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    @Column(name = "progress_count")  // 컬럼명 명시
    private int progressCount;

    @Enumerated(EnumType.STRING)
    private MissionStatus status; // INP, COM, REC

    @Column(name = "created_at")  // 컬럼명 명시
    private LocalDateTime createdAt;

    public boolean increaseProgress() {
        if (this.status == MissionStatus.REC) return false;

        int target = mission.getTargetCount();

        // 목표 이상인 경우 중복 증가 방지
        if (this.progressCount >= target) return false;

        this.progressCount += 1;

        if (this.progressCount >= target) {
            this.status = MissionStatus.COM;
        }

        return true;
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