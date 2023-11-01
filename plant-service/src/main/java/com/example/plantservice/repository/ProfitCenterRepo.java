package com.example.plantservice.repository;

import com.example.plantservice.entity.ProfitCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfitCenterRepo extends JpaRepository<ProfitCenter, Long> {

    Optional<ProfitCenter> findByProfitCenterTitle(String title);


}
