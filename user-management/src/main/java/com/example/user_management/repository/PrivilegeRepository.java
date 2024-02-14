package com.example.user_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.Privilege;

//@Repository(value = "com.example.user_management.repository.PrivilegeRepository")
@Repository("privilegeRepository")
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

	boolean existsByName(String privilegeName);

	boolean existsByNameAndIdNot(String privilegeName, Long id);

}