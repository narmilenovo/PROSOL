package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.EquipmentUnit;

@Repository
public interface EquipmentUnitRepo extends JpaRepository<EquipmentUnit, Long> {

	Optional<EquipmentUnit> findByEquipmentUnitName(String equipmentUnitName);

	boolean existsByEquipmentUnitCodeAndEquipmentUnitName(String code, String name);

	boolean existsByEquipmentUnitCodeAndEquipmentUnitNameAndIdNot(String code, String name, Long id);

	List<EquipmentUnit> findAllByOrderByIdAsc();
}
