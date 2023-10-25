package com.example.vendor_masterservice.repository;

import com.example.vendor_masterservice.entity.VendorMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorMasterRepository extends JpaRepository<VendorMaster, Long> {
    List<VendorMaster> findAllByStatusIsTrue();
}