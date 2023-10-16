package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.MaterialStrategicGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialStrategicGroupRepository extends JpaRepository<MaterialStrategicGroup, Long> {

    List<MaterialStrategicGroup> findAllByMsStatusIsTrue();

    boolean existsByMsCodeOrMsName(String msCode, String msName);

    boolean existsByMsCodeAndIdNotOrMsNameAndIdNot(String msCode, Long id1, String msName, Long id2);
}