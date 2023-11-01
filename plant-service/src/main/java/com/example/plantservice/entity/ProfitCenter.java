package com.example.plantservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitCenter extends BaseEntity<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String profitCenterCode;
    private String profitCenterTitle;
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;
}
