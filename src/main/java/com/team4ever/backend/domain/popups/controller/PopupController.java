package com.team4ever.backend.domain.popups.controller;

import com.team4ever.backend.domain.popups.dto.*;
import com.team4ever.backend.domain.popups.service.PopupService;
import com.team4ever.backend.domain.popups.service.GeolocationService;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;
    private final GeolocationService geolocationService;

    /**
     * 팝업스토어 전체 조회
     */
    @GetMapping
    public ResponseEntity<BaseResponse<List<PopupResponse>>> getAllPopups() {
        List<PopupResponse> result = popupService.getAllPopups();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 팝업스토어 ID로 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PopupResponse>> getPopupById(@PathVariable Integer id) {
        PopupResponse result = popupService.getPopupById(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 수동 좌표로 근처 팝업스토어 조회 (클라리언ㅌ
     */
    @GetMapping("/nearby")
    public ResponseEntity<BaseResponse<List<NearbyPopupResponse>>> getNearbyPopups(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius
    ) {
        NearbyPopupsRequest request = new NearbyPopupsRequest();
        request.setLat(lat);
        request.setLng(lng);
        request.setRadius(radius);

        List<NearbyPopupResponse> results = popupService.getNearbyPopups(request);
        return ResponseEntity.ok(BaseResponse.success(results));
    }

    /**
     * 🎯 IP 기반 자동 위치로 근처 팝업스토어 조회
     */
    @GetMapping("/nearby/location")
    public ResponseEntity<BaseResponse<UserLocationResponse>> getNearbyPopupsAuto(
            HttpServletRequest request,
            @RequestParam(defaultValue = "5.0") Double radius
    ) {
        try {
            // 1. 클라이언트 IP 추출
            String clientIp = getClientIp(request);
            log.info("클라이언트 IP: {}", clientIp);

            // 2. IP 기반 위치 조회
            GeolocationResponse geolocation = geolocationService.getLocationByIp(clientIp);
            GeolocationResponse.GeoLocationData location = geolocation.getGeoLocation();

            log.info("조회된 위치 - lat: {}, lng: {}, 주소: {} {} {}",
                    location.getLat(), location.getLongitude(),
                    location.getR1(), location.getR2(), location.getR3());

            // 3. 근처 팝업스토어 조회
            NearbyPopupsRequest nearbyRequest = new NearbyPopupsRequest();
            nearbyRequest.setLat(location.getLat());
            nearbyRequest.setLng(location.getLongitude());
            nearbyRequest.setRadius(radius);

            List<NearbyPopupResponse> nearbyPopups = popupService.getNearbyPopups(nearbyRequest);

            // 4. 통합 응답 생성
            UserLocationResponse response = UserLocationResponse.builder()
                    .latitude(location.getLat())
                    .longitude(location.getLongitude())
                    .address(String.format("%s %s %s",
                            location.getR1() != null ? location.getR1() : "",
                            location.getR2() != null ? location.getR2() : "",
                            location.getR3() != null ? location.getR3() : "").trim())
                    .nearbyPopups(nearbyPopups)
                    .build();

            return ResponseEntity.ok(BaseResponse.success(response));

        } catch (Exception e) {
            log.error("자동 위치 기반 팝업스토어 조회 실패", e);

            // 5. 실패 시 기본 위치 사용
            return getNearbyPopupsWithDefaultLocation(radius);
        }
    }

    /**
     * 기본 위치로 폴백
     */
    private ResponseEntity<BaseResponse<UserLocationResponse>> getNearbyPopupsWithDefaultLocation(Double radius) {
        log.info("기본 위치 사용");

        // 선릉 멀티캠퍼스 좌표
        double defaultLat = 37.503298369423;
        double defaultLng = 127.04979962846;

        NearbyPopupsRequest nearbyRequest = new NearbyPopupsRequest();
        nearbyRequest.setLat(defaultLat);
        nearbyRequest.setLng(defaultLng);
        nearbyRequest.setRadius(radius);

        List<NearbyPopupResponse> nearbyPopups = popupService.getNearbyPopups(nearbyRequest);

        UserLocationResponse response = UserLocationResponse.builder()
                .latitude(defaultLat)
                .longitude(defaultLng)
                .address("서울특별시 강남구 선릉로 428 (기본 위치)")
                .nearbyPopups(nearbyPopups)
                .build();

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * 클라이언트 실제 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        // 여러 IP가 있는 경우 첫 번째 IP 사용
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.split(",")[0].trim();
        }

        return clientIp;
    }
}