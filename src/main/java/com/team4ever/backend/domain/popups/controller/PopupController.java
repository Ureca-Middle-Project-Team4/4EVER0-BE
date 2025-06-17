package com.team4ever.backend.domain.popups.controller;

import com.team4ever.backend.domain.popups.dto.*;
import com.team4ever.backend.domain.popups.service.PopupService;
import com.team4ever.backend.domain.popups.service.GeolocationService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "팝업 API", description = "팝업스토어 조회 및 위치 기반 탐색 API")
public class PopupController {

    private final PopupService popupService;
    private final GeolocationService geolocationService;

    @Operation(
            summary = "팝업스토어 전체 조회",
            description = """
            등록된 모든 팝업스토어 정보를 조회합니다.

            - 정렬 기준: 기본 등록일 순
            - 로그인 없이 조회 가능
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "요청 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                            {
                              "success": true,
                              "message": "요청 성공",
                              "data": [
                                {
                                  "id": 1,
                                  "name": "카카오프렌즈 팝업",
                                  "location": "홍대입구역 9번 출구",
                                  "startDate": "2025-06-10",
                                  "endDate": "2025-06-20"
                                }
                              ]
                            }
                            """
                    )
            )
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<PopupResponse>>> getAllPopups() {
        List<PopupResponse> result = popupService.getAllPopups();
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    @Operation(
            summary = "특정 팝업스토어 조회",
            description = """
            팝업스토어 ID를 기반으로 상세 정보를 조회합니다.

            - 유효하지 않은 ID일 경우 예외 발생
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "요청 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                            {
                              "success": true,
                              "message": "요청 성공",
                              "data": {
                                "id": 1,
                                "name": "카카오프렌즈 팝업",
                                "location": "홍대입구역 9번 출구",
                                "startDate": "2025-06-10",
                                "endDate": "2025-06-20"
                              }
                            }
                            """
                    )
            )
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PopupResponse>> getPopupById(@PathVariable Integer id) {
        PopupResponse result = popupService.getPopupById(id);
        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /**
     * 수동 좌표로 근처 팝업스토어 조회
     * - 예시: GET /api/popups/nearby?lat=37.5665&lng=126.9780&radius=5.0
     */
    @Operation(
            summary = "수동 좌표 기반 근처 팝업스토어 조회",
            description = """
            사용자가 입력한 위도, 경도, 반경(km)을 기준으로 근처 팝업스토어를 조회합니다.

            예시: `GET /api/popups/nearby?lat=37.5665&lng=126.9780&radius=3.0`
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "요청 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                            {
                              "success": true,
                              "message": "요청 성공",
                              "data": [
                                {
                                  "id": 2,
                                  "name": "무너 피규어 팝업",
                                  "distance": 2.1,
                                  "location": "신촌 현대백화점"
                                }
                              ]
                            }
                            """
                    )
            )
    )
    @GetMapping("/nearby")
    public ResponseEntity<BaseResponse<List<NearbyPopupResponse>>> getNearbyPopups(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius
    ) {
        log.info("수동 좌표 기반 근처 팝업스토어 조회 - lat: {}, lng: {}, radius: {}km", lat, lng, radius);

        NearbyPopupsRequest request = new NearbyPopupsRequest();
        request.setLat(lat);
        request.setLng(lng);
        request.setRadius(radius);

        List<NearbyPopupResponse> results = popupService.getNearbyPopups(request);

        log.info("수동 좌표 기반 조회 완료 - 검색된 팝업스토어 수: {}", results.size());
        return ResponseEntity.ok(BaseResponse.success(results));
    }

    /**
     * IP 기반 자동 위치로 근처 팝업스토어 조회
     */
    @Operation(
            summary = "IP 기반 자동 위치로 근처 팝업스토어 조회",
            description = """
            클라이언트의 IP를 기반으로 위치를 자동 추정하여 주변 팝업스토어를 조회합니다.

            - 실패 시 기본 위치(선릉 멀티캠퍼스)를 기준으로 조회합니다.
            """
    )
    @ApiResponse(
            responseCode = "200",
            description = "요청 성공",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            value = """
                            {
                              "success": true,
                              "message": "요청 성공",
                              "data": {
                                "latitude": 37.5032,
                                "longitude": 127.0497,
                                "address": "서울특별시 강남구 선릉로 428",
                                "nearbyPopups": [
                                  {
                                    "id": 3,
                                    "name": "무너 아트토이 팝업",
                                    "distance": 1.8,
                                    "location": "선릉역 1번 출구 앞"
                                  }
                                ]
                              }
                            }
                            """
                    )
            )
    )
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