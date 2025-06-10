package com.team4ever.backend.domain.mission.service;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.entity.Mission;
import com.team4ever.backend.domain.mission.entity.MissionStatus;
import com.team4ever.backend.domain.mission.entity.UserMission;
import com.team4ever.backend.domain.mission.repository.MissionRepository;
import com.team4ever.backend.domain.mission.repository.UserMissionRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;

    /** 전체 미션 조회: 오늘까지 유효한 미션만 조회 (completedAt >= today) */
    @Transactional(readOnly = true)
    public List<MissionDto> getAllMissions() {
        LocalDate today = LocalDate.now();
        return missionRepository.findAll().stream()
                .filter(m -> !m.getCompletedAt().isBefore(today))
                .map(MissionDto::from)
                .toList();
    }

    /** 미션 진행도 업데이트 */
    @Transactional
    public String updateMissionProgress(Integer userId, Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSION_NOT_FOUND));

        UserMission userMission = userMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseGet(() -> UserMission.builder()
                        .userId(userId)
                        .mission(mission)
                        .progressCount(0)
                        .status(MissionStatus.INP)
                        .createdAt(LocalDateTime.now())
                        .build());

        if (userMission.getStatus() != MissionStatus.REC) {
            userMission.increaseProgress();
            userMissionRepository.save(userMission);
        }

        return "미션 진행도가 업데이트되었습니다.";
    }


    /** 미션 완료 */
//    @Transactional
//    public String receiveMissionReward(Integer userId, Long missionId) {
//        UserMission userMission = userMissionRepository.findByUserIdAndMissionId(userId, missionId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_MISSION_NOT_FOUND));
//
//        userMission.receiveReward(); // 상태 변경 책임을 도메인에게 위임
//
//        Mission mission = userMission.getMission();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        user.addPoint(mission.getRewardPoint()); // 포인트 적립
//
//        // 변경된 엔티티들 저장
//        userMissionRepository.save(userMission);
//        userRepository.save(user);
//
//        return "미션 보상을 수령했습니다.";
//    }
}