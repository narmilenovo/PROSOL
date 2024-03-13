package com.example.dynamic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.entity.FormField;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, Long> {

	List<FormField> findAllByForm_FormName(String formName);

	boolean existsByFieldNameAndForm_FormName(String fieldName, String formName);

	boolean existsByFieldNameAndDataTypeAndForm_FormName(String fieldName, String dataType, String formName);

	boolean existsByFieldNameAndForm_FormNameAndIdNot(String fieldName, String formName, Long id);

}