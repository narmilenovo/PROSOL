package com.example.plantservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageLocation extends BaseEntity<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String storageLocationCode;
    private String storageLocationTitle;
    private Boolean status;

    @ManyToOne
    private Plant plant;
}
