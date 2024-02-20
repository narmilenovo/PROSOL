package com.example.createtemplateservice.jpa.dto.response;

import java.util.Date;
import java.util.List;

import com.example.createtemplateservice.jpa.entity.Type;
import com.example.createtemplateservice.jpa.entity.UpdateAuditHistory;

import lombok.Data;

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
    private Date createdAt;
    private List<UpdateAuditHistory> updateAuditHistories;

}
