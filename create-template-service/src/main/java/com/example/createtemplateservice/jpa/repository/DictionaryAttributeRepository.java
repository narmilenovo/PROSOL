package com.example.createtemplateservice.jpa.repository;

import com.example.createtemplateservice.jpa.entity.DictionaryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryAttributeRepository extends JpaRepository<DictionaryAttribute, Long> {
}