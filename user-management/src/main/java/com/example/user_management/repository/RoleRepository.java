package com.example.user_management.repository;

import com.example.user_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository(value = "com.example.user_management.repository.RoleRepository")
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByStatusIsTrue();

    boolean existsByName(String roleName);


    Optional<Role> findByName(String name);
}