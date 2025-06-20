package com.team4ever.backend.domain.mission.service;

import com.team4ever.backend.domain.mission.dto.MissionDto;
import com.team4ever.backend.domain.mission.dto.UserMissionDto;
import com.team4ever.backend.domain.mission.entity.Mission;
import com.team4ever.backend.domain.mission.entity.MissionStatus;
import com.team4ever.backend.domain.mission.entity.UserMission;
import com.team4ever.backend.domain.mission.repository.MissionRepository;
import com.team4ever.backend.domain.mission.repository.UserMissionRepository;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializeUserMissions(Long userId) {
        // 1. 전체 미션 가져오기
        List<Mission> allMissions = missionRepository.findAll();

        // 2. 이미 존재하는 유저-미션 관계 조회
        List<UserMission> existing = userMissionRepository.findByUserId(userId);
        Set<Long> existingMissionIds = existing.stream()
                .map(um -> um.getMission().getId())
                .collect(Collectors.toSet());

        // 3. 아직 없는 미션만 생성
        List<UserMission> newUserMissions = allMissions.stream()
                .filter(m -> !existingMissionIds.contains(m.getId()))
                .map(m -> UserMission.builder()
                        .userId(userId)
                        .mission(m)
                        .progressCount(0)
                        .status(MissionStatus.INP)
                        .build())
                .toList();

        // 4. 저장
        userMissionRepository.saveAll(newUserMissions);
    }

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
    public String updateMissionProgress(Long userId, Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSION_NOT_FOUND));

        UserMission userMission = userMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElse(null);

        // 없으면 새로 생성
        if (userMission == null) {
            userMission = UserMission.builder()
                    .userId(userId)
                    .mission(mission)
                    .progressCount(0)
                    .status(MissionStatus.INP)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        // 진행도 증가 시도
        boolean updated = userMission.increaseProgress();

        // 무조건 저장 (신규 or 진행도 업데이트 시)
        if (updated || userMission.getId() == null) {
            userMissionRepository.save(userMission);
        }

//        log.info("✅ [미션 진행도 업데이트] userId: {}, missionId: {}, progress: {}/{}, status: {}",
//                userId, missionId, userMission.getProgressCount(),
//                mission.getTargetCount(), userMission.getStatus());

        return "미션 진행도가 업데이트되었습니다.";
    }

    @Transactional(readOnly = true)
    public List<UserMissionDto> getUserMissions(Long userId) {  // Integer → Long
        return userMissionRepository.findByUserIdWithMission(userId).stream()
                .map(UserMissionDto::from)
                .toList();
    }

    /** 미션 완료 */
    @Transactional
    public String receiveMissionReward(Long userId, Long missionId) {
        // 1. UserMission 조회
        UserMission userMission = userMissionRepository.findByUserIdAndMissionId(userId, missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_MISSION_NOT_FOUND));

        // 2. 보상 수령 처리
        userMission.receiveReward();

        // 3. 사용자 포인트 적립
        Mission mission = userMission.getMission();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.addPoint(mission.getRewardPoint()); // User 엔티티에 addPoint 메서드 필요

        // 4. 저장
        userMissionRepository.save(userMission);
        userRepository.save(user);

        return String.format("미션 보상 %d 포인트를 수령했습니다.", mission.getRewardPoint());
    }
}