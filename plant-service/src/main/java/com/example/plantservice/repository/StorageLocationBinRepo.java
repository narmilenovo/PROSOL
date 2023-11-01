package com.example.plantservice.repository;

import com.example.plantservice.entity.StorageBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StorageLocationBinRepo extends JpaRepository<StorageBin, Long> {

    Optional<StorageBin> findByTitle(String title);


}
