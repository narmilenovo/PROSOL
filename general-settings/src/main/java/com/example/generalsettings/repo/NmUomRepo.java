package com.example.generalsettings.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.NmUom;

@Repository
public interface NmUomRepo extends JpaRepository<NmUom, Long>{

	Optional<NmUom> findByName(String name);

}
