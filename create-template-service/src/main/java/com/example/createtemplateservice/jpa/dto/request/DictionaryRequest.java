package com.example.createtemplateservice.jpa.dto.request;

import com.example.createtemplateservice.jpa.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryRequest {
    private String noun;
    private String nounSynonyms;
    private String nmAbbreviation;
    private String modifier;
    private String modifierSynonyms;
    private String nmDefinition;
    private Type type;
    private String similarSearchItems;
    private List<Long> nmUoms;
    private List<DictionaryAttributeRequest> attributes;
    private String image;
}
