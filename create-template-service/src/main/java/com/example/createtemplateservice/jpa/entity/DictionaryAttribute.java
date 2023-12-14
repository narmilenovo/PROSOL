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
public class DictionaryAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "attribute_id")
    private Long attributeId;
    private Integer shortPriority;
    private Boolean mandatory;
    private String definition;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> valueId;
    private Boolean uomMandatory;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> attrUomId;

    // Many-to-One relationship with Dictionary
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

}
