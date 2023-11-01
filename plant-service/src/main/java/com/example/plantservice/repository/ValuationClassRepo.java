package com.example.plantservice.repository;

import com.example.plantservice.entity.ValuationClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValuationClassRepo extends JpaRepository<ValuationClass, Long> {

    Optional<ValuationClass> findByValuationClassName(String valuationClassName);

}
