package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.SubGroupCodes;

@Repository
public interface SubGroupCodesRepo extends JpaRepository<SubGroupCodes, Long> {

    Optional<SubGroupCodes> findBySubGroupName(String subMainGroupTitle);

    boolean existsBySubGroupCodeAndSubGroupName(String code, String name);

    boolean existsBySubGroupCodeAndSubGroupNameAndIdNot(String code, String name, Long id);

    List<SubGroupCodes> findAllByOrderByIdAsc();
}
