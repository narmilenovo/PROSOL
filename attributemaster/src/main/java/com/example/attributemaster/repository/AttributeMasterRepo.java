package com.example.attributemaster.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.attributemaster.entity.AttributeMaster;

@Repository
public interface AttributeMasterRepo extends JpaRepository<AttributeMaster, Long> {

	boolean existsByAttributeName(String name);

	boolean existsByAttributeNameAndIdNot(String name, Long id);

	Optional<AttributeMaster> findByAttributeName(String name);

	List<AttributeMaster> findAllByOrderByIdAsc();
}
