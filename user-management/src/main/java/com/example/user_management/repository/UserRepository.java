package com.example.user_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.User;

//@Repository(value = "com.example.user_management.repository.UserRepository")
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	Optional<User> findByEmail(String email);
}