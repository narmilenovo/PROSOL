package com.example.usersettings.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.usersettings.entity.FontProperty;

@Repository
public interface FontPropertyRepository extends JpaRepository<FontProperty, Long> {

}
