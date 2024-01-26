package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.ScheduleMargin;

@Repository
public interface ScheduleMarginRepo extends JpaRepository<ScheduleMargin, Long> {

	boolean existsByScheduleMarginCodeAndScheduleMarginName(String code, String name);

	boolean existsByScheduleMarginCodeAndScheduleMarginNameAndIdNot(String code, String name, Long id);

	List<ScheduleMargin> findAllByOrderByIdAsc();
}
