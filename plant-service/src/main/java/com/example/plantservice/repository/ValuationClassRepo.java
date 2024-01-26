package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.ValuationClass;

@Repository
public interface ValuationClassRepo extends JpaRepository<ValuationClass, Long> {

    Optional<ValuationClass> findByValuationClassName(String valuationClassName);

    boolean existsByValuationClassCodeAndValuationClassName(String code, String name);

    boolean existsByValuationClassCodeAndValuationClassNameAndIdNot(String code, String name, Long id);

    List<ValuationClass> findAllByOrderByIdAsc();
}
