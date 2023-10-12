package com.example.generalservice.repository;

import com.example.generalservice.entity.AlternateUOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlternateUOMRepository extends JpaRepository<AlternateUOM, Long> {

    List<AlternateUOM> findAllByUomStatusIsTrue();

    boolean existsByUomCode(String uomCode);
}