package com.team4ever.backend.domain.benefit.repository;

import com.team4ever.backend.domain.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    List<Benefit> findAllByBenefitDate(LocalDate benefitDate);

    @Query("SELECT b FROM Benefit b WHERE FUNCTION('YEAR', b.benefitDate) = :year AND FUNCTION('MONTH', b.benefitDate) = :month")
    List<Benefit> findAllByYearAndMonth(int year, int month);

    @Query("SELECT b FROM Benefit b JOIN FETCH b.brand WHERE FUNCTION('YEAR', b.benefitDate) = :year AND FUNCTION('MONTH', b.benefitDate) = :month")
    List<Benefit> findAllByYearAndMonthWithBrand(int year, int month);

    @Query("SELECT b FROM Benefit b JOIN FETCH b.brand WHERE b.benefitDate = :benefitDate")
    List<Benefit> findAllByBenefitDateWithBrand(LocalDate benefitDate);

}

