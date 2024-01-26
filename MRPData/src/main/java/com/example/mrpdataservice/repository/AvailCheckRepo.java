package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.AvailCheck;

@Repository
public interface AvailCheckRepo extends JpaRepository<AvailCheck, Long> {

	boolean existsByAvailCheckCodeAndAvailCheckName(String code, String name);

	boolean existsByAvailCheckCodeAndAvailCheckNameAndIdNot(String code, String name, Long id);

	List<AvailCheck> findAllByOrderByIdAsc();
}
