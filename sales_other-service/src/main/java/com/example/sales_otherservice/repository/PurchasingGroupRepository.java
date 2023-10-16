package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.PurchasingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasingGroupRepository extends JpaRepository<PurchasingGroup, Long> {

    List<PurchasingGroup> findAllByPgStatusIsTrue();

    boolean existsByPgCodeOrPgName(String pgCode, String pgName);

    boolean existsByPgCodeAndIdNotOrPgNameAndIdNot(String pgCode, Long id1, String pgName, Long id2);
}