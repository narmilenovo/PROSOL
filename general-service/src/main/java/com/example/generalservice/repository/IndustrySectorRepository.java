package com.example.generalservice.repository;

import com.example.generalservice.entity.IndustrySector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndustrySectorRepository extends JpaRepository<IndustrySector, Long> {

    List<IndustrySector> findAllBySectorStatusIsTrue();

    boolean existsBySectorCode(String sectorCode);
}