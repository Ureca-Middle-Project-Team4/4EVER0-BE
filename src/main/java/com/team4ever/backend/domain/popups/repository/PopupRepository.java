package com.team4ever.backend.domain.popups.repository;

import com.team4ever.backend.domain.popups.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Integer> {
    List<Popup> findAllByUserId(Integer userId);
}
