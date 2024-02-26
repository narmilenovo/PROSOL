package com.example.user_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.Role;

//@Repository(value = "com.example.user_management.repository.RoleRepository")
@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
	List<Role> findAllByStatusIsTrue();

	boolean existsByName(String roleName);

	boolean existsByNameAndIdNot(String roleName, Long id);

	List<Role> findByPlantId(Long plantId);

}