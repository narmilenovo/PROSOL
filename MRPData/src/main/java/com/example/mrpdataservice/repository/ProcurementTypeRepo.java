package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.ProcurementType;

@Repository
public interface ProcurementTypeRepo extends JpaRepository<ProcurementType, Long> {

	boolean existsByProcurementTypeCodeAndProcurementTypeName(String code, String name);

	boolean existsByProcurementTypeCodeAndProcurementTypeNameAndIdNot(String code, String name, Long id);

	List<ProcurementType> findAllByOrderByIdAsc();
}
