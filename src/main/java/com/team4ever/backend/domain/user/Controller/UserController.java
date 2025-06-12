package com.team4ever.backend.domain.user.Controller;

import com.team4ever.backend.domain.user.Service.UserService;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import com.team4ever.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService; // UserService 인터페이스 주입

    /**
     * 내 구독 상품 목록 조회
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<BaseResponse<UserSubscriptionListResponse>> getUserSubscriptions(
            @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        if (oAuth2User == null || oAuth2User.getAttribute("id") == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String oauthUserId = oAuth2User.getAttribute("id").toString();

        UserSubscriptionListResponse response = userService.getUserSubscriptions(oauthUserId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}