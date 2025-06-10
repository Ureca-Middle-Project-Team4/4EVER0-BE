package com.team4ever.backend.domain.popups.repository;

import com.team4ever.backend.domain.popups.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {

    List<Popup> findAllByUserId(Long userId);

    List<Popup> findAllByUserIdAndIsBookmarkedTrue(Long userId);

    boolean existsByUserIdAndId(Long userId, Long popupId);
}
