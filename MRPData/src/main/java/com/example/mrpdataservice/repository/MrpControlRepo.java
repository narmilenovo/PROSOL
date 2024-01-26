package com.example.mrpdataservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mrpdataservice.entity.MrpControl;

public interface MrpControlRepo extends JpaRepository<MrpControl, Long> {
    Optional<MrpControl> findByMrpControlName(String title);

    boolean existsByMrpControlCodeAndMrpControlName(String code, String name);

    boolean existsByMrpControlCodeAndMrpControlNameAndIdNot(String code, String name, Long id);

    List<MrpControl> findAllByOrderByIdAsc();
}
