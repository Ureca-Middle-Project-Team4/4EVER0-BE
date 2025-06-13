package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.LikedCouponsResponse;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import com.team4ever.backend.domain.user.dto.UserCouponListResponse;


public interface UserService {
	Long createUser(CreateUserRequest req);
	UserResponse getUserByUserId(String oauthId);
	UserResponse getCurrentUser();
	UserSubscriptionListResponse getUserSubscriptions(String oauthUserId);
    LikedCouponsResponse getLikedCoupons(String oauthUserId);
	UserCouponListResponse getMyCoupons(String oauthUserId);

}