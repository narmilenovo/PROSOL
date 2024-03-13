package com.example.usersettings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usersettings.entity.Font;

@Repository
public interface FontRepository extends JpaRepository<Font, Long> {

	Optional<Font> findByCreatedBy(String createdBy);
}
