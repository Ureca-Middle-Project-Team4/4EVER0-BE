package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserResponse;

public interface UserService {
    Long createUser(CreateUserRequest req);
    UserResponse getUserByUserId(String userId);
}