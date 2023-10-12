package com.example.generalservice.repository;

import com.example.generalservice.entity.UnitOfIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitOfIssueRepository extends JpaRepository<UnitOfIssue, Long> {

    List<UnitOfIssue> findAllByUoiStatusIsTrue();

    boolean existsByUoiCode(String uoiCode);
}