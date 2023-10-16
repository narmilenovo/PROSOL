package com.example.sales_otherservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistributionChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dcCode;
    private String dcName;
    private Boolean dcStatus;
    @ManyToOne
    @JoinColumn(name = "sales_organization_id")
    private SalesOrganization salesOrganization;
}
