package com.example.dynamic.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Field extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    private String dataType;
    private String identity;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> pattern;

    private Integer min;
    private Integer max;
    private Integer minLength;
    private Integer maxLength;

    private Boolean required;
    private Boolean extraField;
    private Boolean readable;
    private Boolean writable;
    private Boolean showAsColumn;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<DropDown> dropDowns;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> enums;

    @ManyToOne
    // @JoinColumn(name = "form_id")
    // @JsonIgnore
    private Form form;

}
