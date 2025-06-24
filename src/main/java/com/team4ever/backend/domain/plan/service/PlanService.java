package com.team4ever.backend.domain.plan.service;

import com.team4ever.backend.domain.plan.dto.PlanChangeRequest;
import com.team4ever.backend.domain.plan.dto.PlanChangeResponse;
import com.team4ever.backend.domain.plan.dto.PlanResponse;
import com.team4ever.backend.domain.plan.entity.Plan;
import com.team4ever.backend.domain.plan.repository.PlanRepository;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

	private final PlanRepository planRepository;
	private final UserRepository userRepository;

	/**
	 * 전체 요금제 조회 (활성화된 요금제만)
	 */
	public List<PlanResponse> getAllPlans() {
		try {
			log.info("전체 요금제 조회 시작");

			List<Plan> plans = planRepository.findByIsActiveTrue();

			List<PlanResponse> response = plans.stream()
					.map(this::toPlanResponse)
					.collect(Collectors.toList());

			log.info("전체 요금제 조회 완료 - 조회된 요금제 수: {}", response.size());

			return response;

		} catch (Exception e) {
			log.error("전체 요금제 조회 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 요금제 상세 조회
	 */
	public PlanResponse getPlanDetail(Integer planId) {
		try {
			log.info("요금제 상세 조회 시작 - planId: {}", planId);

			Plan plan = planRepository.findByIdAndIsActiveTrue(planId)
					.orElseThrow(() -> {
						log.error("활성화된 요금제를 찾을 수 없습니다. planId: {}", planId);
						return new CustomException(ErrorCode.PLAN_NOT_FOUND);
					});

			log.info("요금제 상세 조회 완료 - planId: {}, planName: {}",
					plan.getId(), plan.getName());

			return toPlanResponse(plan);

		} catch (CustomException e) {
			log.error("요금제 상세 조회 중 알려진 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("요금제 상세 조회 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 내가 사용중인 요금제 조회
	 */
	public PlanResponse getCurrentPlan(String oauthUserId) {
		try {
			log.info("사용자 요금제 조회 시작 - oauthUserId: {}", oauthUserId);

			// 사용자 조회
			User currentUser = userRepository.findByUserId(oauthUserId)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			Integer planId = currentUser.getPlanId();

			// 요금제가 없는 경우
			if (planId == null) {
				throw new CustomException(ErrorCode.PLAN_NOT_FOUND);
			}

			// 요금제 조회
			Plan plan = planRepository.findByIdAndIsActiveTrue(planId)
					.orElseThrow(() -> {
						log.error("활성화된 요금제를 찾을 수 없습니다. planId: {}", planId);
						return new CustomException(ErrorCode.PLAN_NOT_FOUND);
					});

			log.info("요금제 조회 완료 - planId: {}, planName: {}", plan.getId(), plan.getName());

			return toPlanResponse(plan);

		} catch (CustomException e) {
			log.error("요금제 조회 중 알려진 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("요금제 조회 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 요금제 변경
	 */
	@Transactional
	public PlanChangeResponse changePlan(PlanChangeRequest request, String oauthUserId) {
		try {
			log.info("요금제 변경 요청 시작 - planId: {}, oauthUserId: {}",
					request.getPlanId(), oauthUserId);

			// 사용자 조회
			User currentUser = userRepository.findByUserId(oauthUserId)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			// 변경할 요금제 조회
			Plan newPlan = planRepository.findByIdAndIsActiveTrue(request.getPlanId())
					.orElseThrow(() -> {
						log.error("활성화된 요금제를 찾을 수 없습니다. planId: {}", request.getPlanId());
						return new CustomException(ErrorCode.PLAN_NOT_FOUND);
					});

			// 현재 요금제와 동일한지 확인
			if (currentUser.getPlanId() != null && currentUser.getPlanId().equals(newPlan.getId())) {
				log.warn("현재 사용 중인 요금제와 동일합니다. userId: {}, planId: {}",
						currentUser.getId(), newPlan.getId());
				throw new CustomException(ErrorCode.PLAN_ALREADY_USING);
			}

			// 사용자 요금제 변경
			currentUser.setPlanId(newPlan.getId());
			userRepository.save(currentUser);

			log.info("요금제 변경 완료 - userId: {}, newPlanId: {}",
					currentUser.getId(), newPlan.getId());

			return PlanChangeResponse.builder()
					.planId(newPlan.getId())
					.planName(newPlan.getName())
					.changedAt(LocalDateTime.now())
					.message("요금제가 성공적으로 변경되었습니다.")
					.build();

		} catch (CustomException e) {
			log.error("요금제 변경 중 알려진 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("요금제 변경 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.PLAN_CHANGE_FAILED);
		}
	}

	/**
	 * 요금제 해지
	 */
	@Transactional
	public PlanChangeResponse cancelPlan(String oauthUserId) {
		try {
			log.info("요금제 해지 요청 시작 - oauthUserId: {}", oauthUserId);

			// 사용자 조회
			User currentUser = userRepository.findByUserId(oauthUserId)
					.orElseThrow(() -> {
						log.error("사용자를 찾을 수 없습니다. oauthUserId: {}", oauthUserId);
						return new CustomException(ErrorCode.USER_NOT_FOUND);
					});

			// 현재 요금제가 있는지 확인
			if (currentUser.getPlanId() == null) {
				log.warn("해지할 요금제가 없습니다. userId: {}", currentUser.getId());
				throw new CustomException(ErrorCode.PLAN_NOT_FOUND);
			}

			Integer currentPlanId = currentUser.getPlanId();

			// 요금제 해지 (planId를 null로 설정)
			currentUser.setPlanId(null);
			userRepository.save(currentUser);

			log.info("요금제 해지 완료 - userId: {}, cancelledPlanId: {}",
					currentUser.getId(), currentPlanId);

			return PlanChangeResponse.builder()
					.planId(currentPlanId)
					.planName("해지됨")
					.changedAt(LocalDateTime.now())
					.message("요금제가 성공적으로 해지되었습니다.")
					.build();

		} catch (CustomException e) {
			log.error("요금제 해지 중 알려진 오류 발생: {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("요금제 해지 중 예상치 못한 오류 발생", e);
			throw new CustomException(ErrorCode.PLAN_CANCEL_FAILED);
		}
	}

	/**
	 * Plan 엔티티를 PlanResponse DTO로 변환
	 */
	private PlanResponse toPlanResponse(Plan plan) {
		return PlanResponse.builder()
				.id(plan.getId())
				.name(plan.getName())
				.price(plan.getPrice())
				.description(plan.getDescription())
				.data(plan.getData())
				.speed(plan.getSpeed())
				.sms(plan.getSms())
				.voice(plan.getVoice())
				.shareData(plan.getShareData())
				.build();
	}
}