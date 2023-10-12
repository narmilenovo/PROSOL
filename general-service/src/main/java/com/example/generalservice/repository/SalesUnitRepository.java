package com.example.generalservice.repository;

import com.example.generalservice.entity.SalesUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesUnitRepository extends JpaRepository<SalesUnit, Long> {

    List<SalesUnit> findAllBySalesStatusIsTrue();

    boolean existsBySalesCode(String salesCode);
}