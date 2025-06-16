package com.team4ever.backend.domain.mission.repository;

import com.team4ever.backend.domain.mission.entity.MissionStatus;
import com.team4ever.backend.domain.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    Optional<UserMission> findByUserIdAndMissionId(Long userId, Long missionId);

    List<UserMission> findByUserId(Long userId);

    List<UserMission> findByUserIdAndStatus(Long userId, MissionStatus status);

    @Query("SELECT um FROM UserMission um JOIN FETCH um.mission WHERE um.userId = :userId")
    List<UserMission> findByUserIdWithMission(Long userId);

    int countByUserIdAndStatus(Long userId, MissionStatus status);
}