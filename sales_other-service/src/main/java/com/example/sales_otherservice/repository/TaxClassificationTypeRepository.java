package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.TaxClassificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxClassificationTypeRepository extends JpaRepository<TaxClassificationType, Long> {

    List<TaxClassificationType> findAllByTctStatusIsTrue();

    boolean existsByTctCodeOrTctName(String tctCode, String tctName);

    boolean existsByTctCodeAndIdNotOrTctNameAndIdNot(String tctCode, Long id1, String tctName, Long id2);
}