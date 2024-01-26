package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.PlanningStrategyGrp;

@Repository
public interface PlanningStrgyGrpRepo extends JpaRepository<PlanningStrategyGrp, Long> {

	boolean existsByPlanningStrgGrpCodeAndPlanningStrgGrpName(String code, String name);

	boolean existsByPlanningStrgGrpCodeAndPlanningStrgGrpNameAndIdNot(String code, String name, Long id);

	List<PlanningStrategyGrp> findAllByOrderByIdAsc();
}
