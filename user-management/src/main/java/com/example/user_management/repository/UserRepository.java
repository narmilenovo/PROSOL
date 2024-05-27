package com.example.user_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.User;

@Repository(value = "com.example.user_management.repository.UserRepository")
//@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	Optional<User> findByEmail(String email);

	String findUserByEmail(String email);

	@Query("SELECT u FROM User u WHERE :plantId MEMBER OF u.plantId")
	List<User> findByPlantId(@Param("plantId") List<Long> plantIds);

	List<User> findByAssignees_Role_Id(Long roleId);

	List<User> findByPlantIdInAndAssignees_Role_SubRole_Name(List<Long> plantIds, String roleName);

	@Query("SELECT u FROM User u WHERE : id IS NULL OR u.id = :id")
	Optional<User> searchById(Long id);

}