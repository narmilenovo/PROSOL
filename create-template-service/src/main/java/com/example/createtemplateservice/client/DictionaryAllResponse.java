package com.example.createtemplateservice.client;

import com.example.createtemplateservice.client.GeneralSettings.NmUom;
import com.example.createtemplateservice.jpa.entity.Type;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DictionaryAllResponse {
    private Long id;
    private String noun;
    private String nounSynonyms;
    private String nmAbbreviation;
    private String modifier;
    private String modifierSynonyms;
    private String nmDefinition;
    private Type type;
    private String similarSearchItems;
    private List<NmUom> nmUoms;
    private List<DictionaryAttributeAllResponse> attributes;
    private String image;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
