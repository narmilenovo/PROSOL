package com.example.generalsettings.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.SubMainGroup;

@Repository
public interface SubMainGroupRepo extends JpaRepository<SubMainGroup, Long>{

	Optional<SubMainGroup> findBySubMainGroupTitle(String subMainGroupTitle);

}
