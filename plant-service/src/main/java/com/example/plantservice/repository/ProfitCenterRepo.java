package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.ProfitCenter;

@Repository
public interface ProfitCenterRepo extends JpaRepository<ProfitCenter, Long> {

    Optional<ProfitCenter> findByProfitCenterName(String name);

    boolean existsByProfitCenterCodeAndProfitCenterName(String code, String name);

    boolean existsByProfitCenterCodeAndProfitCenterNameAndIdNot(String code, String name, Long id);

    List<ProfitCenter> findAllByOrderByIdAsc();
}
