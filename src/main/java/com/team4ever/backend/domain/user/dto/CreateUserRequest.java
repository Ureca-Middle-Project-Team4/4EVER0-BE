package com.team4ever.backend.domain.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateUserRequest {
    private Integer planId;

    @NotBlank
    @Size(max = 100)
    private String userId;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private LocalDate birth;
}