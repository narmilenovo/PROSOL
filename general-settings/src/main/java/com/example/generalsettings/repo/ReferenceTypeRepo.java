package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.ReferenceType;

@Repository
public interface ReferenceTypeRepo extends JpaRepository<ReferenceType, Long> {

	Optional<ReferenceType> findByReferenceTypeName(String referenceTypeName);

	boolean existsByReferenceTypeCodeAndReferenceTypeName(String code, String name);

	boolean existsByReferenceTypeCodeAndReferenceTypeNameAndIdNot(String code, String name, Long id);

	List<ReferenceType> findAllByOrderByIdAsc();
}
