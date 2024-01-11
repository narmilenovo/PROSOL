package com.example.dynamic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.entity.DropDown;

@Repository
public interface DropDownRepository extends JpaRepository<DropDown, Long> {

}
