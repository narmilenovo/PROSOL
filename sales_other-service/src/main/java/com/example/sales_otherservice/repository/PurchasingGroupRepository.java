package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.PurchasingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasingGroupRepository extends JpaRepository<PurchasingGroup, Long> {

    List<PurchasingGroup> findAllByPgStatusIsTrue();

    boolean existsByPgCode(String pgCode);
}