package com.example.createtemplateservice.jpa.dto.response;

import com.example.createtemplateservice.jpa.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryResponse {

    private Long id;
    private String noun;
    private String nounSynonyms;
    private String nmAbbreviation;
    private String modifier;
    private String modifierSynonyms;
    private String nmDefinition;
    private Type type;
    private String similarSearchItems;
}
