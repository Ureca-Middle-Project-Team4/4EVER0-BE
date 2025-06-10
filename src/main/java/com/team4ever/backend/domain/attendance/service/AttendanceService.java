package com.team4ever.backend.domain.attendance.service;

import com.team4ever.backend.domain.attendance.entity.Attendance;
import com.team4ever.backend.domain.attendance.dto.AttendanceDto;
import com.team4ever.backend.domain.attendance.repository.AttendanceRepository;
import com.team4ever.backend.global.exception.CustomException;
import com.team4ever.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public AttendanceDto checkToday(Long userId) {
        LocalDate today = LocalDate.now();
        boolean exists = attendanceRepository.existsByUserIdAndCheckedDate(userId, today);
        if (exists) {
            throw new CustomException(ErrorCode.ALREADY_CHECKED);
        }

        Attendance saved = attendanceRepository.save(new Attendance(userId, today));
        return AttendanceDto.from(saved);
    }

    public int getStreak(Long userId) {
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

    public double calculateRate(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1); // 이번 달 1일
        List<Attendance> records = attendanceRepository.findByUserIdAndCheckedDateBetween(userId, startOfMonth, today);

        int attendedDays = records.size();
        long daysPassed = ChronoUnit.DAYS.between(startOfMonth, today) + 1; // 1일 더해야 하므로 +1

        return Math.round(attendedDays * 100.0 / daysPassed) * 10 / 10.0;
    }

}
