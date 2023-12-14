package com.example.createtemplateservice.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @OneToMany(mappedBy = "dictionary", cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true)
    private List<DictionaryAttribute> attributes;


}
