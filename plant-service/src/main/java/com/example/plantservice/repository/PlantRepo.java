package com.example.plantservice.repository;


import com.example.plantservice.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PlantRepo extends JpaRepository<Plant, Long> {

    Optional<Plant> findByPlantName(String plantName);

}
