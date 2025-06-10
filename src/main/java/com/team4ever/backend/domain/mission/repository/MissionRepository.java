package com.team4ever.backend.domain.mission.repository;

import com.team4ever.backend.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    // 기본 메서드만 사용 (findAll)
}
