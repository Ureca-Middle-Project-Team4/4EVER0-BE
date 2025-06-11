package com.team4ever.backend.domain.attendance.service;

import com.team4ever.backend.domain.attendance.entity.Attendance;
import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.repository.AttendanceRepository;
import com.team4ever.backend.domain.user.Entity.User;
import com.team4ever.backend.domain.user.repository.UserRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Transactional
    public AttendanceDto checkToday(Long userId) {
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now();

        // 2. 중복 출석 체크
        boolean exists = attendanceRepository.existsByUserIdAndCheckedDate(userId, today);
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_CHECKED);
        }

        // 3. 출석 저장
        attendanceRepository.save(new Attendance(userId, today));

        // 4. 연속 출석일 수 계산
        int streak = calculateStreak(userId);

        // 5. 유저 테이블에 반영 후 저장
        user.setAttendanceStreak(streak);
        userRepository.save(user);

        return AttendanceDto.from(new Attendance(userId, today));
    }

    public int getStreak(Long userId) {
        return calculateStreak(userId);
    }

    public int calculateStreak(Long userId) {
        List<Attendance> list = attendanceRepository.findAllByUserIdOrderByCheckedDateDesc(userId);
        int streak = 0;
        LocalDate today = LocalDate.now();

        for (Attendance a : list) {
            if (a.getCheckedDate().equals(today.minusDays(streak))) {
                streak++;
            } else {
                break;
            }
        }
        return streak;
    }

}
