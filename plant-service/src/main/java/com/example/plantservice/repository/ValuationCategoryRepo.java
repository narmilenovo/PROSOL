package com.example.plantservice.repository;

import com.example.plantservice.entity.ValuationCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValuationCategoryRepo extends JpaRepository<ValuationCategory, Long> {

    Optional<ValuationCategory> findByValuationCategoryName(String name);

}
