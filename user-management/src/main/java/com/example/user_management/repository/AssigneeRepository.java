package com.example.user_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.Assignee;
import com.example.user_management.entity.Role;

@Repository
public interface AssigneeRepository extends JpaRepository<Assignee, Long> {

	Assignee findByRole(Role role);

	@Query("SELECT a FROM Assignee a WHERE (:roleId IS NULL OR a.role.id = :roleId) "
			+ "AND (:subUserId IS NULL OR a.subUser.id = :subUserId)")
	Assignee findByRole_IdAndSubUser_Id(@Param("roleId") Long role, @Param("subUserId") Long subUser);
}
