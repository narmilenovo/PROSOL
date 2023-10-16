package com.example.sales_otherservice.repository;

import com.example.sales_otherservice.entity.DistributionChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributionChannelRepository extends JpaRepository<DistributionChannel, Long> {

    List<DistributionChannel> findAllByDcStatusIsTrue();

    boolean existsByDcCodeOrDcName(String dcCode, String dcName);

    boolean existsByDcCodeAndIdNotOrDcNameAndIdNot(String dcCode, Long id1, String dcName, Long id2);
}