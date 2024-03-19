package com.example.createtemplateservice.jpa.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryAttribute {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long attributeId;
	private Integer shortPriority;
	private Boolean mandatory;
	private String definition;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<Long> valueId;
	private Boolean uomMandatory;
	@ElementCollection(fetch = FetchType.EAGER)
	private List<Long> attrUomId;

	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "dictionary_id")
	private Dictionary dictionary;

}
