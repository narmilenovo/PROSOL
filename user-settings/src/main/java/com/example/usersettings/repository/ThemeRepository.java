package com.example.usersettings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usersettings.entity.Theme;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {

	Optional<Theme> findByCreatedBy(String createdBy);
}
