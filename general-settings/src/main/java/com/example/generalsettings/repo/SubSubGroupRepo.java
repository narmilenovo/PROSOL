package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.SubSubGroup;

@Repository
public interface SubSubGroupRepo extends JpaRepository<SubSubGroup, Long> {

    Optional<SubSubGroup> findBySubSubGroupName(String title);

    boolean existsBySubSubGroupCodeAndSubSubGroupName(String code, String name);

    boolean existsBySubSubGroupCodeAndSubSubGroupNameAndIdNot(String code, String name, Long id);

    List<SubSubGroup> findAllByOrderByIdAsc();
}
