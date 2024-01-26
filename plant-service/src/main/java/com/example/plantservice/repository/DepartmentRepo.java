package com.example.plantservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.plantservice.entity.Department;

public interface DepartmentRepo extends JpaRepository<Department, Long> {

    boolean existsByDepartmentName(String name);

    boolean existsByDepartmentNameAndIdNot(String name, Long id);

    List<Department> findAllByOrderByIdAsc();
}
