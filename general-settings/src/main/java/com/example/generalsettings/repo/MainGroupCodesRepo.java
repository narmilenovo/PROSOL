package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.MainGroupCodes;

@Repository
public interface MainGroupCodesRepo extends JpaRepository<MainGroupCodes, Long> {

	Optional<MainGroupCodes> findByMainGroupName(String mainGroupName);

	boolean existsByMainGroupCodeAndMainGroupName(String code, String name);

	boolean existsByMainGroupCodeAndMainGroupNameAndIdNot(String code, String name, Long id);

	List<MainGroupCodes> findAllByOrderByIdAsc();

}
