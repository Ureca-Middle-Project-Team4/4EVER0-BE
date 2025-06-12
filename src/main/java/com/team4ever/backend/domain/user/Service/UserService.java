package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;

public interface UserService {
	Long createUser(CreateUserRequest req);
	UserResponse getUserByUserId(String userId);
	UserSubscriptionListResponse getUserSubscriptions(String oauthUserId);
}