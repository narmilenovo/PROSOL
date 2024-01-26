package com.example.mrpdataservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mrpdataservice.entity.LotSize;

@Repository
public interface LotSizeRepo extends JpaRepository<LotSize, Long> {

	boolean existsByLotSizeCodeAndLotSizeName(String code, String name);

	boolean existsByLotSizeCodeAndLotSizeNameAndIdNot(String code, String name, Long id);

	List<LotSize> findAllByOrderByIdAsc();

}
