package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.ValuationCategory;

@Repository
public interface ValuationCategoryRepo extends JpaRepository<ValuationCategory, Long> {

    Optional<ValuationCategory> findByValuationCategoryName(String name);

    boolean existsByValuationCategoryCodeAndValuationCategoryName(String code, String name);

    boolean existsByValuationCategoryCodeAndValuationCategoryNameAndIdNot(String code, String name, Long id);

    List<ValuationCategory> findAllByOrderByIdAsc();
}
