package com.example.createtemplateservice.jpa.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Dictionary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String noun;
    private String nounSynonyms;
    private String nmAbbreviation;
    private String modifier;
    private String modifierSynonyms;
    private String nmDefinition;
    @Enumerated(EnumType.STRING)
    private Type type;
    private String similarSearchItems;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> nmUoms;
    private String image;

    // One-to-Many relationship with DictionaryAttribute
    @OneToMany(mappedBy = "dictionary", cascade = { CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH,
            CascadeType.DETACH })
    private List<DictionaryAttribute> attributes;

}
