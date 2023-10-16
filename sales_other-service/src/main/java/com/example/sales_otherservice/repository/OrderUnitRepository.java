package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderUnitRepository extends JpaRepository<OrderUnit, Long> {

    List<OrderUnit> findAllByOuStatusIsTrue();

    boolean existsByOuCodeOrOuName(String ouCode, String ouName);

    boolean existsByOuCodeAndIdNotOrOuNameAndIdNot(String ouCode, Long id1, String ouName, Long id2);
}