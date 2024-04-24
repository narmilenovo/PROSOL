package com.example.dynamic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.entity.DropDown;
import com.example.dynamic.entity.FormField;

@Repository
public interface DropDownRepository extends JpaRepository<DropDown, Long> {

	List<DropDown> deleteByFormField(FormField formField);
}
