package com.example.generalservice.repository;

import com.example.generalservice.entity.MaterialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialTypeRepository extends JpaRepository<MaterialType, Long> {

    List<MaterialType> findAllByMaterialStatusIsTrue();

    boolean existsByMaterialCodeOrMaterialName(String materialCode, String materialName);

    boolean existsByMaterialCodeAndIdNotOrMaterialNameAndIdNot(String materialCode, Long id1, String materialName, Long id2);
}