package com.team4ever.backend.domain.benefit.repository;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    @EntityGraph(attributePaths = "brand")
    List<Benefit> findAllByDate(LocalDate date);
    List<Benefit> findByDate(LocalDate date);

}
