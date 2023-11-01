package com.example.generalsettings.repo;

import com.example.generalsettings.entity.EquipmentUnit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface EquipmentUnitRepo extends JpaRepository<EquipmentUnit,Long> {

	Optional<EquipmentUnit> findByEquipmentUnitName(String equipmentUnitName);
}
