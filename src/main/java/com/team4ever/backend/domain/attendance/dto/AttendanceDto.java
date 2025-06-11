package com.team4ever.backend.domain.attendance.dto;

import com.team4ever.backend.domain.attendance.entity.Attendance;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class AttendanceDto {
    private Long id;
    private Long userId;
    private LocalDate checkedDate;

    public static AttendanceDto from(Attendance entity) {
        return new AttendanceDto(
          entity.getId(), entity.getUserId(), entity.getCheckedDate()
        );
    }

}
