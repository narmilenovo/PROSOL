package com.example.generalservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalservice.entity.InspectionCode;

@Repository
public interface InspectionCodeRepository extends JpaRepository<InspectionCode, Long> {

	List<InspectionCode> findAllByInCodeStatusIsTrue();

	boolean existsByInCodeCodeOrInCodeName(String inCode, String inName);

	boolean existsByInCodeCodeAndIdNotOrInCodeNameAndIdNot(String inCode, Long id1, String inName, Long id2);
}