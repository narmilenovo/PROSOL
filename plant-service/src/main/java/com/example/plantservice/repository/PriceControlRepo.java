package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.PriceControl;

@Repository
public interface PriceControlRepo extends JpaRepository<PriceControl, Long> {

    Optional<PriceControl> findByPriceControlName(String name);

    boolean existsByPriceControlCodeAndPriceControlName(String code, String name);

    boolean existsByPriceControlCodeAndPriceControlNameAndIdNot(String code, String name, Long id);

    List<PriceControl> findAllByOrderByIdAsc();

}
