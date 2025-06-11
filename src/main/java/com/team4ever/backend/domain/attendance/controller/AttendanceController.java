package com.team4ever.backend.domain.attendance.controller;

import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.dto.AttendanceRequest;
import com.team4ever.backend.domain.attendance.service.AttendanceService;
import com.team4ever.backend.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "연속 출석 일수 조회")
    @GetMapping("/streak")
    public BaseResponse<Integer> getStreak(@RequestParam Long userId) {
        return BaseResponse.success(attendanceService.getStreak(userId));
    }

    @Operation(summary = "연속 출석률 조회")
    @GetMapping("/rate")
    public BaseResponse<Double> getRate(@RequestParam Long userId) {
        double rate = attendanceService.calculateRate(userId);
        return BaseResponse.success(rate);
    }
}
