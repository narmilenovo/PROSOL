package com.example.generalservice.entity;

import java.util.Map;

import com.example.generalservice.configuration.ObjectToJsonConverter;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)

public class BaseUOP extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uopCode;
    private String uopName;
    private Boolean uopStatus;

    @ElementCollection
    @CollectionTable(name = "base_uop_fields", joinColumns = @JoinColumn(name = "base_uop_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @Convert(converter = ObjectToJsonConverter.class)
    private Map<String, Object> dynamicFields;
}
