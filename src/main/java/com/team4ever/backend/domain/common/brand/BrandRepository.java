package com.team4ever.backend.domain.common.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BrandRepository extends JpaRepository<Brand, Integer> {
	List<Brand> findByCategory(String category);

	@Query("SELECT b FROM Brand b WHERE b.category LIKE %:category%")
	List<Brand> findByCategoryContaining(@Param("category") String category);

	@Query("SELECT b FROM Brand b WHERE TRIM(b.category) = TRIM(:category)")
	List<Brand> findByCategoryExact(@Param("category") String category);

	@Query("SELECT b FROM Brand b WHERE b.id = :brandId")
	Brand findBrandId(int brandId);
}
