package com.team4ever.backend.domain.attendance.repository;

import com.team4ever.backend.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByUserIdAndCheckedDate(Long userId, LocalDate date);
    List<Attendance> findAllByUserIdOrderByCheckedDateDesc(Long userId);

    List<Attendance> findByUserIdAndCheckedDateBetween(Long userId, LocalDate start, LocalDate end);
}
