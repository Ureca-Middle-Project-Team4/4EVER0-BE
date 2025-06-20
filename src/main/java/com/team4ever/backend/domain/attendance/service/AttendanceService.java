package com.team4ever.backend.domain.attendance.service;

import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.entity.Attendance;
import com.team4ever.backend.domain.attendance.repository.AttendanceRepository;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    /**
     * 오늘 출석 체크
     */
    @Transactional
    public AttendanceDto checkToday(Long userId) {
        LocalDate today = LocalDate.now();

        // 이미 오늘 출석했는지 확인
        if (attendanceRepository.existsByUserIdAndCheckedDate(userId, today)) {
            throw new CustomException(ErrorCode.ALREADY_CHECKED); // 중복 출석일 때

        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 유저가 없을 때

        // 1. 연속출석이 끊겼는지 체크하고 필요시 초기화
        checkAndResetStreakIfBroken(user);

        // 2. 출석 기록 생성 및 저장
        Attendance attendance = new Attendance(userId, today);
        Attendance savedAttendance = attendanceRepository.save(attendance);

        // 3. 연속출석일수 증가
        user.setAttendanceStreak(user.getAttendanceStreak() + 1);
        userRepository.save(user);

        log.info("유저 {}의 출석이 완료되었습니다. 연속출석: {}일", userId, user.getAttendanceStreak());
        // AttendanceDto 반환
        return AttendanceDto.from(savedAttendance);
    }

    /**
     * 연속출석이 끊겼는지 체크하고 필요시 초기화
     */
    private void checkAndResetStreakIfBroken(User user) {
        Optional<LocalDate> lastAttendanceDate = getLastAttendanceDate(user.getId());

        // 출석 기록이 없는 경우 - 첫 출석이므로 pass
        if (lastAttendanceDate.isEmpty()) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = lastAttendanceDate.get();

        // 마지막 출석일이 어제인 경우 - 연속 기록 유지
        if (lastDate.equals(today.minusDays(1))) {
            return;
        }

        // 마지막 출석일이 어제보다 이전인 경우 - 연속 기록 끊김
        if (lastDate.isBefore(today.minusDays(1))) {
            user.setAttendanceStreak(0);
            userRepository.save(user);
            log.info("유저 {}의 연속출석이 끊어져서 0으로 초기화되었습니다. (마지막 출석: {})",
                    user.getId(), lastDate);
        }
    }

    /**
     * 유저의 마지막 출석일 조회
     */
    private Optional<LocalDate> getLastAttendanceDate(Long userId) {
        return attendanceRepository.findLastAttendanceDateByUserId(userId);
    }

    /**
     * 특정 날짜 출석 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isAttendedOn(Long userId, LocalDate date) {
        return attendanceRepository.existsByUserIdAndCheckedDate(userId, date);
    }

    /**
     * 오늘 출석 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean isAttendedToday(Long userId) {
        return isAttendedOn(userId, LocalDate.now());
    }

    /**
     * 현재 연속출석일수 조회
     */
    @Transactional(readOnly = true)
    public int getCurrentStreak(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)); // 유저 없을 때

        return user.getAttendanceStreak();
    }

    public List<String> getAttendanceDatesForMonth(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<Attendance> records = attendanceRepository.findByUserIdAndCheckedDateBetween(userId, start, end);
        return records.stream()
                .map(record -> record.getCheckedDate().toString()) // "2025-06-15"
                .collect(Collectors.toList());
    }


}