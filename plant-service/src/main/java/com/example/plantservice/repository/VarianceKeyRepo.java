package com.example.plantservice.repository;

import com.example.plantservice.entity.VarianceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VarianceKeyRepo extends JpaRepository<VarianceKey, Long> {

    Optional<VarianceKey> findByVarianceKeyName(String name);


}
