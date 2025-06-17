package com.team4ever.backend.domain.user.Controller;

import com.team4ever.backend.domain.user.Service.UserService;
import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.LikedCouponsResponse;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.team4ever.backend.domain.user.dto.UserCouponListResponse;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 정보 및 개인 데이터 관리 API (인증 필요)")
@SecurityRequirement(name = "cookieAuth")
public class UserController {

    private final UserService svc;
    private final UserRepository userRepository;

    @Operation(
            summary = "신규 회원 생성",
            description = "신규 사용자를 등록합니다. 클라이언트에서 최초 로그인 시 호출됩니다."
    )
    @ApiResponse(responseCode = "200", description = "회원 생성 성공")
    @PostMapping
    public ResponseEntity<Long> createUser(
            @Valid @RequestBody CreateUserRequest req
    ) {
        Long id = svc.createUser(req);
        return ResponseEntity.ok(id);
    }

    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "JWT 기반으로 인증된 현재 사용자의 프로필 정보를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 실패")
    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse dto = svc.getCurrentUser();
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "내 유독 Pick 상품 조회",
            description = "사용자가 구독 중인 유독 Pick 목록을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "유독 Pick 상품 조회 성공")
    @GetMapping("/subscriptions")
    public ResponseEntity<BaseResponse<UserSubscriptionListResponse>> getUserSubscriptions() {
        String userId = getCurrentUserId();
        UserSubscriptionListResponse response = svc.getUserSubscriptions(userId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(
            summary = "좋아요한 쿠폰 목록 조회",
            description = "사용자가 좋아요를 누른 쿠폰들의 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "좋아요 쿠폰 조회 성공")
    @GetMapping("/likes/coupons")
    public ResponseEntity<BaseResponse<LikedCouponsResponse>> getLikedCoupons() {
        String userId = getCurrentUserId();
        LikedCouponsResponse response = svc.getLikedCoupons(userId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(
            summary = "보유중인 쿠폰 조회",
            description = "사용자가 현재 보유 중인 쿠폰 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "보유 쿠폰 조회 성공")
    @GetMapping("/coupons")
    public ResponseEntity<BaseResponse<UserCouponListResponse>> getMyCoupons() {
        String userId = getCurrentUserId();
        UserCouponListResponse response = svc.getMyCoupons(userId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    /**
     * SecurityContext에서 현재 사용자 ID 추출
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("인증되지 않은 사용자의 접근 시도");
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }
        String userId = (String) authentication.getPrincipal();
        log.debug("현재 사용자 ID: {}", userId);
        return userId;
    }
}