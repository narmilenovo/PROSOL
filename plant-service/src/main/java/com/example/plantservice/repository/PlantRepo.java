package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.Plant;

@Repository
public interface PlantRepo extends JpaRepository<Plant, Long> {

	Optional<Plant> findByPlantName(String plantName);

	Optional<Plant> findById(Long plantId);

	boolean existsByPlantCodeAndPlantName(String code, String plantName);

	boolean existsByPlantCodeAndPlantNameAndIdNot(String code, String plantName, Long id);

	List<Plant> findAllByOrderByIdAsc();

}
