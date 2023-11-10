package com.example.valueservice.repository;

import com.example.valueservice.entity.ValueMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueMasterRepository extends JpaRepository<ValueMaster, Long> {
}