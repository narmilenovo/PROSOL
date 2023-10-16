package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.TransportationGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportationGroupRepository extends JpaRepository<TransportationGroup, Long> {
    List<TransportationGroup> findAllByTgStatusIsTrue();

    boolean existsByTgCodeOrTgName(String tgCode, String tgName);

    boolean existsByTgCodeAndIdNotOrTgNameAndIdNot(String tgCode, Long id1, String tgName, Long id2);
}