package com.example.createtemplateservice.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dictionary {
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
}
