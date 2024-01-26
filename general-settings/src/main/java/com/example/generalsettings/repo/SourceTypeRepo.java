package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.SourceType;

@Repository
public interface SourceTypeRepo extends JpaRepository<SourceType, Long> {

	Optional<SourceType> findBySourceTypeName(String sourceTypeName);

	boolean existsBySourceTypeCodeAndSourceTypeName(String code, String name);

	boolean existsBySourceTypeCodeAndSourceTypeNameAndIdNot(String code, String name, Long id);

	List<SourceType> findAllByOrderByIdAsc();
}
