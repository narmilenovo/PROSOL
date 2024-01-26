package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.AttributeUom;

@Repository
public interface AttributeUomRepo extends JpaRepository<AttributeUom, Long> {

    Optional<AttributeUom> findByAttributeUomName(String attributeUomName);

    boolean existsByAttributeUomName(String attributeUomName);

    boolean existsByAttributeUomNameAndIdNot(String name, Long id);

    List<AttributeUom> findAllByOrderByIdAsc();
}
