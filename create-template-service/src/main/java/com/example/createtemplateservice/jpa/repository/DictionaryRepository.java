package com.example.createtemplateservice.jpa.repository;

import com.example.createtemplateservice.jpa.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
}