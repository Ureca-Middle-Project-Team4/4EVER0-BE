package com.team4ever.backend.domain.popups.service;

import com.team4ever.backend.domain.popups.dto.PopupResponse;
import com.team4ever.backend.domain.popups.entity.Popup;
import com.team4ever.backend.domain.popups.repository.PopupRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;

    /** 전체 조회 */
    public List<PopupResponse> getAllPopups() {
        return popupRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /** ID로 조회 */
    public PopupResponse getPopupById(Integer id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));
        return toResponse(popup);
    }

    private PopupResponse toResponse(Popup popup) {
        // description의 \n을 공백으로 치환해야 함
        String cleanedDescription = popup.getDescription() != null
                ? popup.getDescription().replace("\n", " ")
                : "";

        return PopupResponse.builder()
                .id(popup.getId())
                .name(popup.getName())
                .description(cleanedDescription)
                .address(popup.getAddress())
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .imageUrl(popup.getImageUrl())
                .build();
    }

}
