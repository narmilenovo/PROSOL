package com.example.plantservice.repository;

import com.example.plantservice.entity.PriceControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceControlRepo extends JpaRepository<PriceControl, Long> {

    Optional<PriceControl> findByPriceControlName(String name);


}
