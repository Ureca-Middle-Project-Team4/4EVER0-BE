package com.team4ever.backend.domain.popups.service;

import com.team4ever.backend.domain.popups.dto.PopupResponse;
import com.team4ever.backend.domain.popups.dto.NearbyPopupsRequest;
import com.team4ever.backend.domain.popups.dto.NearbyPopupResponse;
import com.team4ever.backend.domain.popups.entity.Popup;
import com.team4ever.backend.domain.popups.repository.PopupRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.util.GeoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;

    /**
     * 팝업스토어 전체 조회
     */
    public List<PopupResponse> getAllPopups() {
        return popupRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 팝업스토어 ID로 조회
     */
    public PopupResponse getPopupById(Integer id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POPUP_NOT_FOUND));
        return toResponse(popup);
    }

    /**
     * 근처 팝업스토어 조회
     */
    public List<NearbyPopupResponse> getNearbyPopups(NearbyPopupsRequest request) {
        try {
            log.info("근처 팝업스토어 조회 시작 - lat: {}, lng: {}, radius: {}km",
                    request.getLat(), request.getLng(), request.getRadius());

            // 검색 범위의 경계 좌표 계산
            double[] boundingBox = GeoUtils.getBoundingBox(
                    request.getLat(), request.getLng(), request.getRadius());

            log.debug("검색 경계 - minLat: {}, maxLat: {}, minLng: {}, maxLng: {}",
                    boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]);

            // 경계 내의 팝업스토어 조회
            List<Popup> nearbyPopups = popupRepository.findPopupsInBoundingBox(
                    boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3]);

            // 정확한 거리 계산 및 필터링
            List<NearbyPopupResponse> results = nearbyPopups.stream()
                    .map(popup -> {
                        double distance = GeoUtils.calculateDistance(
                                request.getLat(), request.getLng(),
                                popup.getLatitude(), popup.getLongitude());

                        return NearbyPopupResponse.builder()
                                .id(popup.getId())
                                .name(popup.getName())
                                .description(cleanDescription(popup.getDescription()))
                                .address(popup.getAddress())
                                .latitude(popup.getLatitude())
                                .longitude(popup.getLongitude())
                                .imageUrl(popup.getImageUrl())
                                .distanceKm(Math.round(distance * 100.0) / 100.0) // 소수점 2자리
                                .build();
                    })
                    .filter(popup -> popup.getDistanceKm() <= request.getRadius()) // 정확한 거리로 필터링
                    .sorted((p1, p2) -> Double.compare(p1.getDistanceKm(), p2.getDistanceKm())) // 거리순 정렬
                    .collect(Collectors.toList());

            log.info("근처 팝업스토어 조회 완료 - 검색된 수: {}", results.size());

            return results;

        } catch (Exception e) {
            log.error("근처 팝업스토어 조회 중 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Popup 엔티티를 PopupResponse DTO로 변환
     */
    private PopupResponse toResponse(Popup popup) {
        return PopupResponse.builder()
                .id(popup.getId())
                .name(popup.getName())
                .description(cleanDescription(popup.getDescription()))
                .address(popup.getAddress())
                .latitude(popup.getLatitude())
                .longitude(popup.getLongitude())
                .imageUrl(popup.getImageUrl())
                .build();
    }

    /**
     * 설명 텍스트 정리 (개행문자 제거)
     */
    private String cleanDescription(String description) {
        return description != null ? description.replace("\n", " ") : "";
    }
}