package com.example.plantservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.plantservice.entity.StorageBin;

@Repository
public interface StorageLocationBinRepo extends JpaRepository<StorageBin, Long> {

    Optional<StorageBin> findByStorageBinName(String title);

    boolean existsByStorageBinCodeAndStorageBinName(String code, String name);

    boolean existsByStorageBinCodeAndStorageBinNameAndIdNot(String code, String name, Long id);

    List<StorageBin> findAllByOrderByIdAsc();

}
