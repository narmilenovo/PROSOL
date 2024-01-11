package com.example.valueservice.entity;

import java.util.Map;

import com.example.valueservice.configuration.ObjectToJsonConverter;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValueMaster extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String value;
    private String abbreviation;
    private Long abbreviationUnit;
    private String equivalent;
    private Long equivalentUnit;
    private String likelyWords;

    @ElementCollection
    @CollectionTable(name = "value_master_fields", joinColumns = @JoinColumn(name = "value_master_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @Convert(converter = ObjectToJsonConverter.class)
    private Map<String, Object> dynamicFields;
}
