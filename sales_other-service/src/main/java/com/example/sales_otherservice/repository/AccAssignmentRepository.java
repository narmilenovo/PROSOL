package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.AccAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccAssignmentRepository extends JpaRepository<AccAssignment, Long> {

    List<AccAssignment> findAllByAccStatusIsTrue();

    boolean existsByAccCodeOrAccName(String accCode, String accName);

    boolean existsByAccCodeAndIdNotOrAccNameAndIdNot(String accCode, Long id1, String accName, Long id2);
}