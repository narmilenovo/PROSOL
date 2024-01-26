package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.VarianceKey;

@Repository
public interface VarianceKeyRepo extends JpaRepository<VarianceKey, Long> {

    Optional<VarianceKey> findByVarianceKeyName(String name);

    boolean existsByVarianceKeyCodeAndVarianceKeyName(String code, String name);

    boolean existsByVarianceKeyCodeAndVarianceKeyNameAndIdNot(String code, String name, Long id);

    List<VarianceKey> findAllByOrderByIdAsc();

}
