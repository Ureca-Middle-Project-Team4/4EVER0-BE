package com.team4ever.backend.domain.user.Controller;

import com.team4ever.backend.domain.user.Service.UserService;
import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.LikedCouponsResponse;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import com.team4ever.backend.global.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService svc;
    private final JwtTokenProvider jwtProvider;

    @Operation(summary = "새 회원 만들기")
    // 신규 회원 생성
    @PostMapping
    public ResponseEntity<Long> createUser(
            @Valid @RequestBody CreateUserRequest req
    ) {
        Long id = svc.createUser(req);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "내 정보 조회")
    // userId로 회원 정보 조회
    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse dto = svc.getCurrentUser();
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "내 구독 상품 목록 조회")
    @GetMapping("/subscriptions")
    public ResponseEntity<BaseResponse<UserSubscriptionListResponse>> getUserSubscriptions(
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String oauthUserId = oAuth2User.getAttribute("id").toString();

        UserSubscriptionListResponse response = svc.getUserSubscriptions(oauthUserId); // userService → svc로 변경!
        return ResponseEntity.ok(BaseResponse.success(response));
    }

    @Operation(summary = "내 좋아요한 쿠폰 목록 조회")
    @GetMapping("/likes/coupons")
    public ResponseEntity<BaseResponse<LikedCouponsResponse>> getLikedCoupons(
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String oauthUserId = oAuth2User.getAttribute("id").toString();

        LikedCouponsResponse response = svc.getLikedCoupons(oauthUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}