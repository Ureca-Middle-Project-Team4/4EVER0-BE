package com.team4ever.backend.domain.user.Service;

import com.team4ever.backend.domain.user.dto.CreateUserRequest;
import com.team4ever.backend.domain.user.dto.UserResponse;
import com.team4ever.backend.domain.user.dto.UserSubscriptionDto;
import com.team4ever.backend.domain.user.dto.UserSubscriptionListResponse;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.domain.subscriptions.repository.UserSubscriptionCombinationRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final int DEFAULT_PLAN_ID = 0;

    private final UserRepository repo;
    private final UserSubscriptionCombinationRepository userSubscriptionCombinationRepository;

    public UserServiceImpl(UserRepository repo, UserSubscriptionCombinationRepository userSubscriptionCombinationRepository) {
        this.repo = repo;
        this.userSubscriptionCombinationRepository = userSubscriptionCombinationRepository;
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
    public void getUserByUserId(String userId) {
        User u = repo.findByUserId(userId)
                //Exception 나중에 정의해서 바꾸기
                .orElseThrow(() -> new IllegalArgumentException("해당 userId를 찾을 수 없습니다."));
        UserResponse.builder()
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

    // 사용자 구독 목록 조회
    @Transactional(readOnly = true)
    @Override
    public UserSubscriptionListResponse getUserSubscriptions(String oauthUserId) {
        try {
            log.info("사용자 구독 목록 조회 시작 - oauthUserId: {}", oauthUserId);

            // 사용자 조회
            User currentUser = repo.findByUserId(oauthUserId)
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
                        return new CustomException(ErrorCode.USER_NOT_FOUND);
                    });

            Long userId = currentUser.getId();
            log.info("조회 요청 사용자 PK: {}", userId);

            // 구독 목록 조회
            List<UserSubscriptionDto> subscriptions = userSubscriptionCombinationRepository
                    .findUserSubscriptionsWithDetails(userId);

            log.info("사용자 구독 목록 조회 완료 - userId: {}, 구독 수: {}", userId, subscriptions.size());

            return UserSubscriptionListResponse.builder()
                    .total(subscriptions.size())
                    .combinations(subscriptions)
                    .build();

        } catch (CustomException e) {
            log.error("사용자 구독 목록 조회 중 알려진 오류 발생: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("사용자 구독 목록 조회 중 예상치 못한 오류 발생", e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}