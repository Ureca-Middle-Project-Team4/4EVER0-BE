package com.team4ever.backend.domain.attendance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRequest {
    @JsonProperty("user_id")
    private Long userId;
}
