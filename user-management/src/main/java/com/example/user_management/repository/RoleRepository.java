package com.example.user_management.repository;

import com.example.user_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository(value = "com.example.user_management.repository.RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByStatusIsTrue();

    boolean existsByName(String roleName);

    boolean existsByNameAndIdNot(String roleName, Long id);

}