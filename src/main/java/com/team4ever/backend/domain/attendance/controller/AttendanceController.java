package com.team4ever.backend.domain.attendance.controller;

import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.service.AttendanceService;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
@Tag(name = "출석 API", description = "출석 체크 및 출석 현황 관리 API (인증 필요)")
@SecurityRequirement(name = "cookieAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @Operation(
            summary = "오늘 출석하기",
            description = """
            현재 로그인한 사용자의 오늘 출석을 체크합니다.
            
            **출석 혜택:**
            - 연속 출석 일수 증가
            - 출석 포인트 적립
            - 출석 보상 지급
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "출석 체크 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "출석 체크가 완료되었습니다.",
                          "data": {
                            "id": 123,
                            "user_id": 1,
                            "checked_date": "2025-06-15"
                          }
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 출석 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": false,
                          "message": "오늘은 이미 출석하셨습니다.",
                          "data": null
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public BaseResponse<AttendanceDto> checkToday() {
        log.info("출석 체크 요청");

        Long userId = getCurrentUserIdAsLong();
        AttendanceDto result = attendanceService.checkToday(userId);

        log.info("출석 체크 완료 - userId: {}", userId);
        return BaseResponse.success(result);
    }

    @Operation(
            summary = "내 연속 출석 일수 조회",
            description = """
            현재 로그인한 사용자의 연속 출석 일수를 조회합니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "요청 성공",
                          "data": 15
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/streak")
    public BaseResponse<Integer> getStreak() {
        log.info("연속 출석 일수 조회 요청");

        Long userId = getCurrentUserIdAsLong();
        Integer streak = attendanceService.getCurrentStreak(userId);

        log.info("연속 출석 일수 조회 완료 - userId: {}, streak: {}", userId, streak);
        return BaseResponse.success(streak);
    }

    @Operation(
            summary = "오늘 출석 여부 확인",
            description = """
            현재 로그인한 사용자의 오늘 출석 여부를 확인합니다.
            
            **응답값:**
            - true: 오늘 출석 완료
            - false: 오늘 아직 출석하지 않음
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "출석 완료한 경우",
                                    value = """
                        {
                          "success": true,
                          "message": "요청 성공,
                          "data": true
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "아직 출석하지 않은 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                        {
                          "success": true,
                          "message": "요청 성공",
                          "data": false
                        }
                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/today")
    public BaseResponse<Boolean> isAttendedToday() {
        log.info("오늘 출석 여부 확인 요청");

        Long userId = getCurrentUserIdAsLong();
        Boolean isAttended = attendanceService.isAttendedToday(userId);

        log.info("출석 여부 확인 완료 - userId: {}, attended: {}", userId, isAttended);
        return BaseResponse.success(isAttended);
    }

    /**
     * SecurityContext에서 현재 사용자 ID 추출 (JWT 인터셉터에서 설정됨)
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("인증되지 않은 사용자의 출석 API 접근 시도");
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        String userId = (String) authentication.getPrincipal();
        log.debug("현재 사용자 ID: {}", userId);
        return userId;
    }

    /**
     * JWT에서 추출한 User.userId(String)로 User 엔티티를 조회하여 PK(Long) 반환
     */
    private Long getCurrentUserIdAsLong() {
        try {
            String userIdStr = getCurrentUserId();

            // User.userId(String)로 User 엔티티 조회
            User user = userRepository.findByUserId(userIdStr)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIdStr));

            Long userPkId = user.getId();
            log.debug("JWT userId: {} -> User PK: {}", userIdStr, userPkId);

            return userPkId;

        } catch (Exception e) {
            log.error("사용자 ID 변환 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("사용자 정보를 조회할 수 없습니다.");
        }
    }

    @Operation(
            summary = "월별 출석 기록 조회",
            description = "연-월(YYYY-MM)을 기준으로 출석 도장이 찍힐 날짜 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                {
                  "success": true,
                  "message": "요청 성공",
                  "data": ["2025-06-01", "2025-06-03", "2025-06-14"]
                }
                """
                            )
                    )
            )
    })
    @GetMapping("/month")
    public BaseResponse<List<String>> getMonthlyAttendance(
            @RequestParam int year,
            @RequestParam int month
    ) {
        log.info("월별 출석 기록 조회 요청: year={}, month={}", year, month);
        Long userId = getCurrentUserIdAsLong();
        List<String> attendanceDates = attendanceService.getAttendanceDatesForMonth(userId, year, month);
        return BaseResponse.success(attendanceDates);
    }


}