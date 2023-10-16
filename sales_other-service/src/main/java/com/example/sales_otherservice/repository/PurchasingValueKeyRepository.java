package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.PurchasingValueKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasingValueKeyRepository extends JpaRepository<PurchasingValueKey, Long> {

    List<PurchasingValueKey> findAllByPvkStatusIsTrue();

    boolean existsByPvkCodeOrPvkName(String pvkCode, String pvkName);

    boolean existsByPvkCodeAndIdNotOrPvkNameAndIdNot(String pvkCode, Long id1, String pvkName, Long id2);

}