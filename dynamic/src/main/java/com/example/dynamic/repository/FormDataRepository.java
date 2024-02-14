package com.example.dynamic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dynamic.entity.FormData;

@Repository
public interface FormDataRepository extends JpaRepository<FormData, Long> {

}
