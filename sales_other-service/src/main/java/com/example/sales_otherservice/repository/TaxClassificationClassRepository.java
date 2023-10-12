package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.TaxClassificationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxClassificationClassRepository extends JpaRepository<TaxClassificationClass, Long> {

    List<TaxClassificationClass> findAllByTccStatusIsTrue();

    boolean existsByTccCode(String tccCode);
}