package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.StorageLocation;

@Repository
public interface StorageLocationRepo extends JpaRepository<StorageLocation, Long> {

    Optional<StorageLocation> findByStorageLocationName(String title);

    Optional<StorageLocation> findById(Long id);

    boolean existsByStorageLocationCodeAndStorageLocationName(String code, String name);

    boolean existsByStorageLocationCodeAndStorageLocationNameAndIdNot(String code, String name, Long id);

    List<StorageLocation> findByPlant_PlantName(String plantName);

    List<StorageLocation> findAllByOrderByIdAsc();

}
