package com.example.user_management.repository;

import com.example.user_management.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository(value = "com.example.user_management.repository.PrivilegeRepository")
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    boolean existsByName(String privilegeName);

    boolean existsByNameAndIdNot(String privilegeName, Long id);
}