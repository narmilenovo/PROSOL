package com.example.requestitemservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.requestitemservice.entity.RequestItem;

@Repository
public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {
}
