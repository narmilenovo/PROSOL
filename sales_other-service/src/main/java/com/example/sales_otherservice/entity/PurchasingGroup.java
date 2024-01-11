package com.example.sales_otherservice.entity;

import java.util.Map;

import com.example.sales_otherservice.configuration.ObjectToJsonConverter;

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
public class PurchasingGroup extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pgCode;
    private String pgName;
    private Boolean pgStatus;

    @ElementCollection
    @CollectionTable(name = "purchasing_grp_fields", joinColumns = @JoinColumn(name = "purchasing_grp_id"))
    @MapKeyColumn(name = "field_name")
    @Column(name = "field_value")
    @Convert(converter = ObjectToJsonConverter.class)
    private Map<String, Object> dynamicFields;
}
