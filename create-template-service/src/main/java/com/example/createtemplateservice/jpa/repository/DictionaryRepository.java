package com.example.createtemplateservice.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.createtemplateservice.jpa.entity.Dictionary;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

	@Query("SELECT d.noun FROM Dictionary d WHERE LOWER(d.noun) LIKE LOWER(concat(?1, '%'))")
	List<String> findNounSuggestionsByPrefix(String noun);

	@Query("SELECT d.modifier FROM Dictionary d WHERE LOWER(d.noun) = LOWER(?1)")
	List<String> findModifiersByNoun(String noun);

	Dictionary findByNounAndModifier(String noun, String modifier);

	boolean existsByNounAndModifier(String noun, String modifier);

	boolean existsByNounAndModifierAndIdNot(String noun, String modifier, Long id);
}