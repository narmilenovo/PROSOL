package com.example.plantservice.repository;


import com.example.plantservice.entity.StorageLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageLocationRepo extends JpaRepository<StorageLocation, Long> {

    Optional<StorageLocation> findByStorageLocationTitle(String title);
    Optional<StorageLocation> findById(Long id);


}
