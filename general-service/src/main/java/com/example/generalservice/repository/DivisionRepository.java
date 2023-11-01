package com.example.generalservice.repository;

import com.example.generalservice.entity.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    List<Division> findAllByDivStatusIsTrue();

    boolean existsByDivCodeOrDivName(String divCode, String divName);

    boolean existsByDivCodeAndIdNotOrDivNameAndIdNot(String divCode, Long id1, String divName, Long id2);
}