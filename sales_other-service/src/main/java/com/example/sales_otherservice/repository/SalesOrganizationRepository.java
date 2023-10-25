package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.SalesOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesOrganizationRepository extends JpaRepository<SalesOrganization, Long> {

    List<SalesOrganization> findAllBySoStatusIsTrue();

    boolean existsBySoCodeOrSoName(String soCode, String soName);

    boolean existsBySoCodeAndIdNotOrSoNameAndIdNot(String soCode, Long id1, String soName, Long id2);

}