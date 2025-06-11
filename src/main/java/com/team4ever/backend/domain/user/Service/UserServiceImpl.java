package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final int DEFAULT_PLAN_ID = 0;
    private final UserRepository repo;

    public UserServiceImpl(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Long createUser(CreateUserRequest req) {
        if (repo.existsByUserId(req.getUserId())) {
            //Exception 나중에 정의해서 바꾸기
            throw new IllegalArgumentException("이미 존재하는 userId 입니다.");
        }

        Integer planId = req.getPlanId() != null
                ? req.getPlanId()
                : DEFAULT_PLAN_ID;
        User u = User.builder()
                .planId(req.getPlanId())
                .userId(req.getUserId())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .name(req.getName())
                .birth(req.getBirth())
                .attendanceStreak(0)
                .point(0)
                .build();
        return repo.save(u).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUserId(String userId) {
        User u = repo.findByUserId(userId)
                //Exception 나중에 정의해서 바꾸기
                .orElseThrow(() -> new IllegalArgumentException("해당 userId를 찾을 수 없습니다."));
        return UserResponse.builder()
                .id(u.getId())
                .planId(u.getPlanId())
                .userId(u.getUserId())
                .email(u.getEmail())
                .phoneNumber(u.getPhoneNumber())
                .name(u.getName())
                .birth(u.getBirth())
                .attendanceStreak(u.getAttendanceStreak())
                .point(u.getPoint())
                .build();
    }
}