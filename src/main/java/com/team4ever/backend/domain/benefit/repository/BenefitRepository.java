package com.team4ever.backend.domain.benefit.repository;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    List<Benefit> findAllByDate(LocalDate date);
}
