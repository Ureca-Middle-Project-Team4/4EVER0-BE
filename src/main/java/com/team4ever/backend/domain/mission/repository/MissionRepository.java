package com.team4ever.backend.domain.mission.repository;

import com.team4ever.backend.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
