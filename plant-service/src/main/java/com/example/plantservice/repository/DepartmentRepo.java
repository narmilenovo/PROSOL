package com.example.plantservice.repository;

import com.example.plantservice.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepo extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentName(String name);

}
