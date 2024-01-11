package com.example.dynamic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.entity.Form;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

	Optional<Form> findByFormName(String formName);

}
