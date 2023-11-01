package com.example.generalservice.repository;

import com.example.generalservice.entity.BaseUOP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseUOPRepository extends JpaRepository<BaseUOP, Long> {
    List<BaseUOP> findAllByUopStatusIsTrue();

    boolean existsByUopCodeOrUopName(String uopCode, String uopName);

    boolean existsByUopCodeAndIdNotOrUopNameAndIdNot(String uopCode, Long id1, String uopName, Long id2);
}