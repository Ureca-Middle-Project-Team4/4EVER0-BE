package com.team4ever.backend.domain.attendance.controller;

import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.dto.AttendanceRequest;
import com.team4ever.backend.domain.attendance.service.AttendanceService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "오늘 출석하기")
    @PostMapping
    public BaseResponse<AttendanceDto> checkToday(@RequestBody AttendanceRequest req) {
        return BaseResponse.success(attendanceService.checkToday(req.getUserId()));
    }

    @Operation(summary = "내 연속 출석 일수 조회")
    @GetMapping("/streak")
    public BaseResponse<Integer> getStreak(@RequestParam Long userId) {
        return BaseResponse.success(attendanceService.getCurrentStreak(userId));
    }

    @Operation(summary = "오늘 출석 여부 확인")
    @GetMapping("/today")
    public BaseResponse<Boolean> isAttendedToday(@RequestParam Long userId) {
        return BaseResponse.success(attendanceService.isAttendedToday(userId));
    }
}