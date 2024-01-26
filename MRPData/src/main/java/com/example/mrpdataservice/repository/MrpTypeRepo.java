package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.MrpType;

@Repository
public interface MrpTypeRepo extends JpaRepository<MrpType, Long> {

	boolean existsByMrpTypeCodeAndMrpTypeName(String code, String name);

	boolean existsByMrpTypeCodeAndMrpTypeNameAndIdNot(String code, String name, Long id);

	List<MrpType> findAllByOrderByIdAsc();
}
