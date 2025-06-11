package com.team4ever.backend.domain.attendance.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "attendances")
@Getter
@NoArgsConstructor
public class Attendance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private LocalDate checkedDate;

    public Attendance(Long userId, LocalDate checkedDate) {
        this.userId = userId;
        this.checkedDate = checkedDate;
    }
}
