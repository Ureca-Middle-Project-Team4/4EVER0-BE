package com.team4ever.backend.domain.popups.repository;

import com.team4ever.backend.domain.popups.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Integer> {

	/**
	 * 지정된 경계 내의 팝업스토어 조회 (위도, 경도 범위)
	 */
	@Query("""
        SELECT p FROM Popup p 
        WHERE p.latitude BETWEEN :minLat AND :maxLat 
        AND p.longitude BETWEEN :minLng AND :maxLng
        AND p.latitude IS NOT NULL 
        AND p.longitude IS NOT NULL
        ORDER BY p.id
    """)
	List<Popup> findPopupsInBoundingBox(
			@Param("minLat") double minLat,
			@Param("maxLat") double maxLat,
			@Param("minLng") double minLng,
			@Param("maxLng") double maxLng
	);
}