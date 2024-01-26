package com.example.generalsettings.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.generalsettings.entity.Hsn;

@Repository
public interface HsnRepo extends JpaRepository<Hsn, Long> {

	Optional<Hsn> findByHsnCode(String hsnId);

	boolean existsByHsnCodeAndHsnDesc(String hsnCode, String hsnDesc);

	boolean existsByHsnCodeAndHsnDescAndIdNot(String hsnCode, String hsnDesc, Long id);

	List<Hsn> findAllByOrderByIdAsc();
}
