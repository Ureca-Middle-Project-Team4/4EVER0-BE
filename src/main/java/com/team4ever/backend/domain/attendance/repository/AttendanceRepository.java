package com.team4ever.backend.domain.attendance.repository;

import com.team4ever.backend.domain.attendance.entity.Attendance;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findAllByUserIdOrderByCheckedDateDesc(Long userId);
    List<Attendance> findByUserIdAndCheckedDateBetween(Long userId, LocalDate start, LocalDate end);

    /** 특정 유저의 마지막 출석일 조회 */
    @Query("SELECT MAX(a.checkedDate) FROM Attendance a WHERE a.userId = :userId")
    Optional<LocalDate> findLastAttendanceDateByUserId(@Param("userId") Long userId);

    /** 특정 유저의 특정 날짜 출석 여부 확인 */
    boolean existsByUserIdAndCheckedDate(Long userId, LocalDate checkedDate);
}
