package com.example.generalservice.repository;

import com.example.generalservice.entity.InspectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionTypeRepository extends JpaRepository<InspectionType, Long> {

    List<InspectionType> findAllByInTypeStatusIsTrue();

    boolean existsByInTypeCodeOrInTypeName(String typeCode, String typeName);

    boolean existsByInTypeCodeAndIdNotOrInTypeNameAndIdNot(String typeCode, Long id1, String typeName, Long id2);
}