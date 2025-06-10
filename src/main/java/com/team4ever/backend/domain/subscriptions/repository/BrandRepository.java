package com.team4ever.backend.domain.subscriptions.repository;

import com.team4ever.backend.domain.subscriptions.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
	List<Brand> findByCategory(String category);

	// 대소문자 구분 없이 검색
	List<Brand> findByCategoryIgnoreCase(String category);

	// LIKE 검색 (부분 일치)
	@Query("SELECT b FROM Brand b WHERE b.category LIKE %:category%")
	List<Brand> findByCategoryContaining(@Param("category") String category);

	// 정확히 일치하는 카테고리 검색 (트림 포함)
	@Query("SELECT b FROM Brand b WHERE TRIM(b.category) = TRIM(:category)")
	List<Brand> findByCategoryExact(@Param("category") String category);
}