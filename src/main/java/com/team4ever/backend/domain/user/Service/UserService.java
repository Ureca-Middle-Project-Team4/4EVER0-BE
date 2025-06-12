package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    Long createUser(CreateUserRequest req);
    void getUserByUserId(String userId);

	// 새로 추가: 사용자 구독 목록 조회
	@Transactional(readOnly = true)
	UserSubscriptionListResponse getUserSubscriptions(String oauthUserId);
}