package com.example.createtemplateservice.jpa.repository;

import com.example.createtemplateservice.jpa.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    @Query("SELECT d.noun FROM Dictionary d WHERE LOWER(d.noun) LIKE LOWER(concat(?1, '%'))")
    List<String> findNounSuggestionsByPrefix(String noun);

    @Query("SELECT d.modifier FROM Dictionary d WHERE LOWER(d.noun) = LOWER(?1)")
    List<String> findModifiersByNoun(String noun);
}