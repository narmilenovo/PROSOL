package com.example.createtemplateservice.jpa.dto.response;

import com.example.createtemplateservice.jpa.entity.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
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
    private List<Long> nmUoms;
    private List<DictionaryAttributeResponse> attributes;
    private String image;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
