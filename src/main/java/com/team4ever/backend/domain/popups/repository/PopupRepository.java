package com.team4ever.backend.domain.popups.repository;

import com.team4ever.backend.domain.popups.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Integer> {
    // 기본 CRUD 메서드는 JpaRepository에서 제공
}