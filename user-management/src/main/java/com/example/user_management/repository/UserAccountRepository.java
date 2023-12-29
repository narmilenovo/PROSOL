package com.example.user_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.user_management.entity.UserAccount;

//@Repository(value = "com.example.user_management.repository.UserAccountRepository")
@Repository("userAccountRepository")
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByToken(String token);

	@Query("SELECT ua FROM UserAccount ua WHERE ua.expireAt < :currentTimestamp")
	List<UserAccount> findExpiredTokens(@Param("currentTimestamp") long currentTimestamp);
}