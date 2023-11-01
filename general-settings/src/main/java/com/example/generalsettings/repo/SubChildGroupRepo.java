package com.example.generalsettings.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.SubChildGroup;


@Repository
public interface SubChildGroupRepo extends JpaRepository<SubChildGroup, Long>{

	Optional<SubChildGroup> findByTitle(String title);

}
