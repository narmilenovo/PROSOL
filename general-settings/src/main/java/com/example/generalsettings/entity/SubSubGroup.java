package com.example.generalsettings.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubSubGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String subSubGroupCode;
    private String subSubGroupName;
    private Boolean subSubGroupStatus;

    @ManyToOne
    @JoinColumn(name = "mainGroupCodes_id")
    private MainGroupCodes mainGroupCodesId;

    @ManyToOne
    @JoinColumn(name = "subGroup_id")
    private SubGroupCodes subGroupCodesId;
}
