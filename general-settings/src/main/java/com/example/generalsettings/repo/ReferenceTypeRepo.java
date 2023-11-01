package com.example.generalsettings.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.ReferenceType;
@Repository
public interface ReferenceTypeRepo extends JpaRepository<ReferenceType, Long> {

	Optional<ReferenceType> findByReferenceTypeName(String referenceTypeName);

}
