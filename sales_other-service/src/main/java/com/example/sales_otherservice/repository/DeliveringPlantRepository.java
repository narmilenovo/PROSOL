package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.DeliveringPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveringPlantRepository extends JpaRepository<DeliveringPlant, Long> {

    List<DeliveringPlant> findAllByDpStatusIsTrue();

    boolean existsByDpCode(String dpCode);
}