package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.ItemCategoryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCategoryGroupRepository extends JpaRepository<ItemCategoryGroup, Long> {

    List<ItemCategoryGroup> findAllByIcgStatusIsTrue();

    boolean existsByIcgCodeOrIcgName(String icgCode, String icgName);

    boolean existsByIcgCodeAndIdNotOrIcgNameAndIdNot(String icgCode, Long id1, String icgName, Long id2);
}