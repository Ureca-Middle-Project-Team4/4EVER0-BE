package com.team4ever.backend.domain.mission.repository;

import com.team4ever.backend.domain.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    Optional<UserMission> findByUserIdAndMissionId(Integer userId, Long missionId);
}