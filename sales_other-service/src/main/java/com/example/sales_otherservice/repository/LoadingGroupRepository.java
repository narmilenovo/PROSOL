package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.LoadingGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadingGroupRepository extends JpaRepository<LoadingGroup, Long> {

    List<LoadingGroup> findAllByLgStatusIsTrue();

    boolean existsByLgCode(String lgCode);
}