package com.example.plantservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageBin extends BaseEntity<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String title;
    private Boolean status;
    @ManyToOne
    private Plant plant;
    @ManyToOne
    private StorageLocation storageLocation;

}
